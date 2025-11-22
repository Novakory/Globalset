
import { generateError, getGenericError } from '../utils/errorsUtil.js';
import { pool } from '../bd.js';
import sql from 'mssql'
import { query, response } from 'express';
import { printQuery, encriptador } from '../utils/dbUtil.js';
import { formatDB } from '../utils/dateUtil.js';

const sameColumnsSelected = `
      p.cve_control,
      CONVERT(VARCHAR,p.fec_propuesta, 103) AS fec_propuesta,
      p.importe,
      p.id_divisa,
      p.concepto,
      p.no_empresa,
      p.desc_empresa,
      p.id_banco,
      p.desc_banco,
      p.id_chequera,
      p.nivel_autorizacion,
      p.usuario_uno,
      p.usuario_dos,
      p.usuario_tres,
      p.motivo_rechazo,
      p.status,
      COALESCE((SELECT clave_usuario FROM usuarios WHERE id_usuario = p.usuario_uno),'') AS nombre_usuario_uno,
      COALESCE((SELECT clave_usuario FROM usuarios WHERE id_usuario = p.usuario_dos),'') AS nombre_usuario_dos,
      COALESCE((SELECT clave_usuario FROM usuarios WHERE id_usuario = p.usuario_tres),'') AS nombre_usuario_tres
`
export const getPropuestasByUser = async (req, res) => {//ok
  try {
    const connection = await pool;
    const { clave_usuario } = req.params;

    const query = `
      SELECT 
      ${sameColumnsSelected}
      FROM propuestas p
      JOIN usuarios u ON u.clave_usuario = @clave_usuario
      CROSS APPLY STRING_SPLIT(u.empresas, ',') e
      WHERE 
      p.usuario_uno IS NOT NULL
      AND e.value = CAST(p.no_empresa AS VARCHAR(10))
    `;//TODO sin validar que el usuario_uno sea null? para el regreso lo siga viendo?
    // console.log("query getPropuestasByUser: ", query)
    const result = await connection.request()
      .input("clave_usuario", sql.TYPES.VarChar, clave_usuario)
      .query(query)

    res.json(result.recordset);

  } catch (error) {
    console.error("Error en getPropuestasByUser:", error);
    return res.status(500).json({ SUCCESS: false, MESSAGE: "Error en el servidor" });
  }
}

export const getPropuestasPendientesByUser = async (req, res) => {//ok
  try {
    const connection = await pool;

    const { clave_usuario } = req.params;

    const query = `
      SELECT 
      ${sameColumnsSelected}
      FROM propuestas p
      JOIN usuarios u ON u.clave_usuario = @clave_usuario
      CROSS APPLY STRING_SPLIT(u.empresas, ',') e
      WHERE p.usuario_uno IS NOT NULL
      AND e.value = CAST(p.no_empresa AS VARCHAR(10))
      AND (
        (CASE WHEN p.nivel_autorizacion = 3 AND (p.usuario_dos IS NULL OR p.usuario_tres IS NULL) THEN 1 ELSE 0 END) = 1
        OR (CASE WHEN p.nivel_autorizacion = 2 AND p.usuario_dos IS NULL THEN 1 ELSE 0 END) = 1
      )
    `;
    // console.log("query getPropuestasPendientesByUser: ", query)
    const result = await connection.request()
      .input("clave_usuario", sql.TYPES.VarChar, clave_usuario)
      .query(query)

    res.json(result.recordset);

  } catch (error) {
    console.error("Error en getPropuestasPendientesByUser:", error);
    return res.status(500).json({ SUCCESS: false, MESSAGE: "Error en el servidor" });
  }
}

//TODO validar multiples propuestas
//TODO validar multiples propuestas con una con error para ver el funcionamiento del rollback
//ACTUALIZA CON PROPUESTAS DE LA APP MOVIL
export const autorizarPropuestas = async (req, res) => {//ok
  const connection = await pool;
  const transaction = new sql.Transaction(connection);
  try {
    transaction.begin();

    const { propuestas, id_usuario } = req.body;
    console.log({
      propuestas,
      id_usuario
    })

    const clavesControl = propuestas.map(propuesta => "'" + propuesta.cve_control + "'").join(",")

    //VALIDAR SI ALGUNA  TIENE NULL el usuario_uno__________________________________
    let queryValidacion = `
        SELECT cve_control FROM propuestas WHERE cve_control IN(${clavesControl}) AND usuario_uno IS NULL
      `;
    // printQuery("autorizarPropuestas_1", queryValidacion);
    const { recordset } = await connection.request().query(queryValidacion);
    if (recordset.length > 0) {
      throw generateError(201, `La propuesta ${recordset[0].cve_control} no tiene la primera autorización`);
    }

    //VALIDAR SI HAN CAMBIADO?
    for (let propuesta of propuestas) {
      const queryValidacion2 = `
        SELECT 
          cve_control,
          id_banco,
          id_chequera,

          importe,
          nivel_autorizacion,
          usuario_uno,
          usuario_dos,
          usuario_tres
        FROM propuestas
        WHERE cve_control = '${propuesta.cve_control}'
      `;

      // printQuery("autorizarPropuestas_2", queryValidacion2);
      const { recordset } = await connection.request().query(queryValidacion2);

      if (recordset.length === 0) {
        throw generateError(201, `La propuesta ${propuesta.cve_control} no existe en la base de datos, favor de revisarla nuevamente`);
      }
      const row = recordset[0];

      if (row.id_banco === null || row.id_banco === '' || row.id_chequera === null || row.id_chequera === '')
        throw generateError(201, `La propuesta ${propuesta.cve_control} no tiene banco ó chequera pagadora por tanto no se puede autorizar.`);

      //TODO validar que contenga banco y chequera beneficiaria
      //TODO validar que contenga banco y chequera pagadora y beneficiaria

      const camposInvalidos = [];

      if (row.importe !== propuesta.importe) camposInvalidos.push('Importe');
      if (row.id_chequera !== propuesta.id_chequera) camposInvalidos.push('Chequera');
      if (row.nivel_autorizacion !== propuesta.nivel_autorizacion) camposInvalidos.push('Nivel autorización');
      if (row.usuario_uno !== propuesta.usuario_uno) camposInvalidos.push('Usuario uno');
      if (row.usuario_dos !== propuesta.usuario_dos) camposInvalidos.push('Usuario dos');
      if (row.usuario_tres !== propuesta.usuario_tres) camposInvalidos.push('Usuario tres');
      if (camposInvalidos.length > 0) {
        throw generateError(201, `La propuesta ${propuesta.cve_control} tiene diferencias en los campos: ${camposInvalidos.join(', ')}, favor de revisarla nuevamente`);
      }
    }

    const usuario = await connection.request()
      .input("id_usuario", sql.Int, id_usuario)
      .query(`SELECT facultad_mancomunada, facultad_total FROM usuarios where id_usuario = @id_usuario`)

    if (usuario.recordset.length === 0) throw generateError(201, `No se encontro el usuario: ${id_usuario} en la base de datos`);
    const facultadMancomunada = usuario.recordset[0].facultad_mancomunada;
    const facultadTotal = usuario.recordset[0].facultad_total;

    if (facultadMancomunada === false && facultadTotal === false) throw generateError(201, `No tiene facultad para autorizar`);

    //3 - ACTUALLIZAR________________________________________________-
    for (const propuesta of propuestas) {
      if (facultadTotal === true) {
        let query = "";
        if (propuesta.nivel_autorizacion === 2 && propuesta.usuario_dos === null) {
          query = `UPDATE propuestas SET status = 1, motivo_rechazo = null, usuario_dos = @id_usuario  WHERE cve_control = @cve_control`;
        } else if (propuesta.nivel_autorizacion === 3 && propuesta.usuario_dos === null) {
          query = `UPDATE propuestas SET status = 1, motivo_rechazo = null, usuario_dos = @id_usuario, usuario_tres = @id_usuario  WHERE cve_control = @cve_control`;
        } else if (propuesta.nivel_autorizacion === 3 && propuesta.usuario_tres === null) {
          query = `UPDATE propuestas SET status = 1, motivo_rechazo = null, usuario_tres = @id_usuario  WHERE cve_control = @cve_control`;
        }

        console.log("query autorizarPropuestas: ", query)
        const responseUpdate = await connection.request()
          .input("id_usuario", sql.Int, id_usuario)
          .input("cve_control", sql.VarChar, propuesta.cve_control)
          .query(query)
        console.log("responseUpdate: ", responseUpdate)
      }
      else if (facultadMancomunada === true) {
        //VALIDA NIVEL AUTORIZACION
        let usuarioCampo = "";
        if (propuesta.nivel_autorizacion === 1) {
          usuarioCampo = null;
        } else if (propuesta.nivel_autorizacion === 2) {
          usuarioCampo = propuesta.usuario_dos === null ? 'usuario_dos' : null;
        } else if (propuesta.nivel_autorizacion === 3) {
          usuarioCampo = propuesta.usuario_dos === null ? 'usuario_dos' :
            propuesta.usuario_tres === null ? 'usuario_tres' : null;
        }
        if (usuarioCampo === null)
          throw generateError(201, `La propuesta con clave de control ${propuesta.cve_control} ya tiene su número maximo de autorizaciones (${propuesta.nivel_autorizacion})`);

        //TODO VALIDAR SI TIENE FACULTAD MANCOMUNAD - SI HA AUTORIZADO ALGUN NIVEL ANTERIOR MANDAR ERROR
        if (propuesta.usuario_uno === id_usuario || propuesta.usuario_dos === id_usuario) {
          throw generateError(201, `No puedes tener más de una autorización en la propuesta con clave de control ${propuesta.cve_control}`);
        }

        const query = `UPDATE propuestas SET status = 1, motivo_rechazo = null, ${usuarioCampo} = @id_usuario WHERE cve_control = @cve_control`;
        console.log("query autorizarPropuestas: ", query)
        const responseUpdate = await connection.request()
          .input("id_usuario", sql.Int, id_usuario)
          .input("cve_control", sql.VarChar, propuesta.cve_control)
          .query(query)
        console.log("responseUpdate: ", responseUpdate)
      }
    }

    transaction.commit();

    res.json({ SUCCESS: true, MESSAGE: "" });

  } catch (error) {
    console.error("Error en autorizarPropuestas:", error);
    transaction.rollback();
    return getGenericError(res, error)
  }
}
//ACTUALIZA CON PROPUESTAS DE LA APP MOVIL
export const desautorizarPropuestas = async (req, res) => {//ok
  const connection = await pool;
  const transaction = new sql.Transaction(connection);
  try {
    transaction.begin();
    const { propuestas, id_usuario } = req.body;
    console.log({
      propuestas,
      id_usuario
    })

    //1-VALIDAR SI HAN CAMBIADO?
    for (let propuesta of propuestas) {
      const queryValidacion2 = `
        SELECT 
          cve_control,
          importe,
          id_chequera,
          nivel_autorizacion,
          usuario_uno,
          usuario_dos,
          usuario_tres
        FROM propuestas
        WHERE cve_control = '${propuesta.cve_control}'
      `;

      // printQuery("autorizarPropuestas_1", queryValidacion2);
      const response = await connection.request().query(queryValidacion2);

      if (response.recordset.length === 0)
        throw generateError(201, `La propuesta ${propuesta.cve_control} no existe en la base de datos, favor de revisarla nuevamente`)

      const row = response.recordset[0];
      const camposInvalidos = [];

      if (row.importe !== propuesta.importe) camposInvalidos.push('Importe');
      if (row.id_chequera !== propuesta.id_chequera) camposInvalidos.push('Chequera');
      if (row.nivel_autorizacion !== propuesta.nivel_autorizacion) camposInvalidos.push('Nivel autorización');
      if (row.usuario_uno !== propuesta.usuario_uno) camposInvalidos.push('Usuario uno');
      if (row.usuario_dos !== propuesta.usuario_dos) camposInvalidos.push('Usuario dos');
      if (row.usuario_tres !== propuesta.usuario_tres) camposInvalidos.push('Usuario tres');
      if (camposInvalidos.length > 0)
        throw generateError(201, `La propuesta ${propuesta.cve_control} tiene diferencias en los campos: ${camposInvalidos.join(', ')}, favor de revisarla nuevamente`)

      //VALIDAR SI EL USUARIO 1 ES NULL
      if (propuesta.usuario_uno === null)
        throw generateError(201, `Nada para desautorizar en la propuesta ${propuesta.cve_control}`)


      //VALIDA SI TIENE NIVEL 1 AUTORIZACION
      if (propuesta.nivel_autorizacion === 1) {
        throw generateError(201, `No puedes desautorizar propuestas con nivel 1 de autorización - propuesta ${propuesta.cve_control}`)
      }


      //VALIDAR SI EL ULTIMO USUARIO AUTORIZADO 3 Ó 2 SON DEL USUARIO DESAUTORIZADOR
      if (row.usuario_tres !== null) {
        //TODO facultad autorizacion/desautorizacion total?
        if (row.usuario_tres !== id_usuario)
          throw generateError(201, `No puedes desautorizar la propuesta ${propuesta.cve_control} ya que no eres el último autorizador`)

      } else if (row.usuario_dos !== null) {
        if (row.usuario_dos !== id_usuario)
          throw generateError(201, `No puedes desautorizar la propuesta ${propuesta.cve_control} ya que no eres el último autorizador`)

      }
      else if (row.usuario_tres === null && row.usuario_uno === null) {
        throw generateError(201, `No puedes desautorizar el nivel 1 - propuesta ${propuesta.cve_control}`)
      }
    }

    //2 - ACTUALLIZAR________________________________________________-
    let updates = new Array();
    for (const propuesta of propuestas) {

      //VALIDA NIVEL AUTORIZACION
      let usuarioCampo = null;
      if (propuesta.nivel_autorizacion === 2) {
        usuarioCampo = propuesta.usuario_dos !== null ? "usuario_dos" : null
      } else if (propuesta.nivel_autorizacion === 3) {
        usuarioCampo = propuesta.usuario_tres !== null ? 'usuario_tres' :
          propuesta.usuario_dos !== null ? 'usuario_dos' : null;
      }
      if (usuarioCampo === null)
        throw generateError(201, `Error en validar la propuesta ${propuesta.cve_control}`)

      const query = `UPDATE propuestas SET status = 1, ${usuarioCampo} = null WHERE cve_control = @cve_control`
      console.log("query desautorizarPropuestas: ", query)
      const responseUpdate = await connection.request()
        .input("cve_control", sql.VarChar, propuesta.cve_control)
        .query(query)
      console.log("responseUpdate: ", responseUpdate)
      //TODO validar respuesta en rowsAffected sino rollback?
    }

    transaction.commit();
    res.json({ SUCCESS: true, MESSAGE: "" });

  } catch (error) {
    console.error("Error en desautorizarPropuestas:", error);
    transaction.rollback();
    const message = error.MESSAGE || error
    const status = error.status || 500;
    return res.status(status).json({ SUCCESS: false, MESSAGE: message });
  }
}

export const rechazarPropuestas = async (req, res) => {//ok
  const connection = await pool;
  const transaction = new sql.Transaction(connection);
  try {
    transaction.begin();
    const { propuestas, motivoRechazo } = req.body;

    //TODO validar con express datos de entrada

    const clavesControl = propuestas.map(propuesta => "'" + propuesta.cve_control + "'").join(",")
    const query = `UPDATE propuestas SET usuario_uno = null, usuario_dos = null, usuario_tres = null, 
      motivo_rechazo = @motivo_rechazo, status = 1
      WHERE cve_control in(${clavesControl})
    `
    console.log("query rechazarPropuestas: ", query)

    const response = await connection.request()
      .input("motivo_rechazo", sql.VarChar, motivoRechazo)
      .query(query);
    console.log("repsuesta : ", response)

    transaction.commit();
    res.json({ SUCCESS: true, MESSAGE: "" });
  } catch (error) {
    console.error("Error en rechazarPropuestas:", error);
    transaction.rollback();
    return getGenericError(res, error);
  }
}







export const autorizarPropuestas2 = async (req, res) => {
  const connection = await pool.getConnection(); //la conexion se usa para poder trabajar con transacciones
  try {
    const { propuestas, id_usuario } = req.body;

    // Obtenemos las claves de control
    const clavesControl = propuestas.map(propuesta => propuesta.cve_control);


    // Validar si alguna propuesta no tiene `usuario_uno`
    const [validacion1] = await connection.query(
      `SELECT cve_control FROM propuestas 
       WHERE cve_control IN (?) AND usuario_uno IS NULL`,
      [clavesControl]
    );
    if (validacion1.length > 0) {
      return res.status(400).json({
        SUCCESS: false,
        MESSAGE: `La propuesta ${validacion1[0].cve_control} no tiene la primera autorización`,
      });
    }

    // Validar si alguna propuesta ha cambiado
    const cambios = propuestas.map(propuesta => [
      propuesta.cve_control,
      propuesta.importe,
      propuesta.id_chequera,
      propuesta.nivel_autorizacion,
      propuesta.usuario_uno,
      propuesta.usuario_dos,
      propuesta.usuario_tres,
    ]);

    const [validacion2] = await connection.query(
      `SELECT cve_control FROM propuestas 
       WHERE (cve_control, importe, id_chequera, nivel_autorizacion, usuario_uno, usuario_dos, usuario_tres) 
       NOT IN (?)`,
      [cambios]
    );

    if (validacion2.length > 0) {
      return res.status(400).json({
        SUCCESS: false,
        MESSAGE: `La propuesta ${validacion2[0].cve_control} ha cambiado en alguno de sus campos, favor de revisarla nuevamente`,
      });
    }

    // Preparar actualizaciones
    const updates = propuestas.map(propuesta => {
      const usuarioCampo = propuesta.usuario_dos === null ? 'usuario_dos' : 'usuario_tres';
      return {
        query: `UPDATE propuestas SET status = 1, ${usuarioCampo} = ? WHERE cve_control = ?`,
        params: [id_usuario, propuesta.cve_control],
      };
    });

    // Ejecutar todas las actualizaciones
    // Iniciar transacción
    await connection.beginTransaction();

    for (const update of updates) {
      await connection.query(update.query, update.params);
    }

    // Confirmar transacción
    await connection.commit();

    res.json({ SUCCESS: true, MESSAGE: "" });

  } catch (error) {
    await connection.rollback();
    console.error("Error en autorizarPropuestas2:", error);
    return res.status(500).json({ SUCCESS: false, MESSAGE: "Error en el servidor" });
  } finally {
    connection.release();
  }
}