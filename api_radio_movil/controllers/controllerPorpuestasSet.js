
import { generateError } from '../utils/errorsUtil.js';
import { pool } from '../bd.js';
import { query, response } from 'express';
import { queryWithParams } from '../utils/dbUtil.js';
import { formatDB } from '../utils/dateUtil.js';
import { wsClients } from '../utils/wsServer.js'
import { wsSendAlert } from '../utils/wsService.js'
import sql from 'mssql'
import "../utils/logger.js";

const MAX_PARAMS = 2000;//MAXIMO DE PARAMETROS PERMITIDOS PARA PROCESAR SIMULTANEAMENTE EN SQL SERVER
//ACTUALIA CON LAS PROPUESTAS DEL SET
export const updatePropuestas = async (req, res) => {//ok
  try {
    const { propuestas } = req.body;
    const connection = await pool;

    const COLUMNS = 15;
    //PROCESARA DE A 140 PROPUESTAS POR EJECUCION PARA EVITAR REVASAR LOS MAXIMOS PARAMETROS PERMITIDOS POR SQL SERVE
    const MAX_BATCH = Math.floor(MAX_PARAMS / COLUMNS);

    if (propuestas == null || propuestas.length == 0) return res.status(201).json({ SUCCESS: false, MESSAGE: "Nada para actualizar en updatePropuestas" })

    let countRowsAffected = 0;
    async function procesarBatch(batch) {
      const values = batch.map(() => '(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)').join(',\n');
      const params = batch.flatMap(propuesta => [//todos los subarrays los deja en uno solo
        propuesta.cve_control,
        formatDB(propuesta.fec_propuesta),
        propuesta.importe, propuesta.id_divisa, propuesta.concepto, propuesta.no_empresa, propuesta.desc_empresa, propuesta.id_banco, propuesta.desc_banco, propuesta.id_chequera, propuesta.nivel_autorizacion, propuesta.usuario_uno, propuesta.usuario_dos, propuesta.usuario_tres, propuesta.motivo_rechazo
      ]);

      //TODO ver como hacer que las propuestas que tengan un status en 1 no sean modificadas
      const query = `
        MERGE INTO propuestas AS target
        USING (VALUES
          ${values}
        ) AS source (cve_control, fec_propuesta, importe, id_divisa, concepto, no_empresa, desc_empresa, id_banco, desc_banco, id_chequera,nivel_autorizacion, usuario_uno, usuario_dos,usuario_tres,motivo_rechazo)
        ON target.cve_control = source.cve_control
        WHEN MATCHED AND target.status = 0 AND(
          ISNULL(source.fec_propuesta,'') <> ISNULL(target.fec_propuesta,'')
          OR ISNULL(source.importe,'') <> ISNULL(target.importe,'')
          OR ISNULL(source.id_divisa,'') <> ISNULL(target.id_divisa,'')
          OR ISNULL(source.concepto,'') <> ISNULL(target.concepto,'')
          OR ISNULL(source.no_empresa,'') <> ISNULL(target.no_empresa,'')
          OR ISNULL(source.desc_empresa,'') <> ISNULL(target.desc_empresa,'')
          OR ISNULL(source.id_banco,'') <> ISNULL(target.id_banco,'')
          OR ISNULL(source.desc_banco,'') <> ISNULL(target.desc_banco,'')
          OR ISNULL(source.id_chequera,'') <> ISNULL(target.id_chequera,'')
          OR ISNULL(source.nivel_autorizacion,'') <> ISNULL(target.nivel_autorizacion,'')
          OR ISNULL(source.usuario_uno,'') <> ISNULL(target.usuario_uno,'')
          OR ISNULL(source.usuario_dos,'') <> ISNULL(target.usuario_dos,'')
          OR ISNULL(source.usuario_tres,'') <> ISNULL(target.usuario_tres,'')
          OR ISNULL(source.motivo_rechazo,'') <> ISNULL(target.motivo_rechazo,'')
        ) THEN UPDATE SET
            fec_propuesta = CASE WHEN ISNULL(source.fec_propuesta,'') <> ISNULL(target.fec_propuesta,'') THEN source.fec_propuesta ELSE target.fec_propuesta END,
            importe = CASE WHEN ISNULL(source.importe,'') <> ISNULL(target.importe,'') THEN source.importe ELSE target.importe END,
            id_divisa = CASE WHEN ISNULL(source.id_divisa,'') <> ISNULL(target.id_divisa,'') THEN source.id_divisa ELSE target.id_divisa END,
            concepto = CASE WHEN ISNULL(source.concepto,'') <> ISNULL(target.concepto,'') THEN source.concepto ELSE target.concepto END,
            no_empresa = CASE WHEN ISNULL(source.no_empresa,'') <> ISNULL(target.no_empresa,'') THEN source.no_empresa ELSE target.no_empresa END,
            desc_empresa = CASE WHEN ISNULL(source.desc_empresa,'') <> ISNULL(target.desc_empresa,'') THEN source.desc_empresa ELSE target.desc_empresa END,
            id_banco = CASE WHEN ISNULL(source.id_banco,'') <> ISNULL(target.id_banco,'') THEN source.id_banco ELSE target.id_banco END,
            desc_banco = CASE WHEN ISNULL(source.desc_banco,'') <> ISNULL(target.desc_banco,'') THEN source.desc_banco ELSE target.desc_banco END,
            id_chequera = CASE WHEN ISNULL(source.id_chequera,'') <> ISNULL(target.id_chequera,'') THEN source.id_chequera ELSE target.id_chequera END,
            nivel_autorizacion = CASE WHEN ISNULL(source.nivel_autorizacion,'') <> ISNULL(target.nivel_autorizacion,'') THEN source.nivel_autorizacion ELSE target.nivel_autorizacion END,
            usuario_uno = CASE WHEN ISNULL(source.usuario_uno,'') <> ISNULL(target.usuario_uno,'') THEN source.usuario_uno ELSE target.usuario_uno END,
            usuario_dos = CASE WHEN ISNULL(source.usuario_dos,'') <> ISNULL(target.usuario_dos,'') THEN source.usuario_dos ELSE target.usuario_dos END,
            usuario_tres = CASE WHEN ISNULL(source.usuario_tres,'') <> ISNULL(target.usuario_tres,'') THEN source.usuario_tres ELSE target.usuario_tres END,
            motivo_rechazo = CASE WHEN ISNULL(source.motivo_rechazo,'') <> ISNULL(target.motivo_rechazo,'') THEN source.motivo_rechazo ELSE target.motivo_rechazo END
        WHEN NOT MATCHED THEN
          INSERT (cve_control, fec_propuesta, importe, id_divisa, concepto, no_empresa, desc_empresa, id_banco, desc_banco, id_chequera,nivel_autorizacion, usuario_uno, usuario_dos,usuario_tres,motivo_rechazo)
          VALUES (source.cve_control, source.fec_propuesta, source.importe, source.id_divisa, source.concepto, source.no_empresa, source.desc_empresa, source.id_banco, source.desc_banco, source.id_chequera,source.nivel_autorizacion, source.usuario_uno, source.usuario_dos,source.usuario_tres,source.motivo_rechazo);
      `;
      const response = await queryWithParams(connection, query, params);
      countRowsAffected += parseInt(response.rowsAffected[0]) || 0;
    }
    for (let i = 0; i < propuestas.length; i += MAX_BATCH) {
      const batch = propuestas.slice(i, i + MAX_BATCH);
      await procesarBatch(batch);
    }

    console.log("cambios propuestas: ", countRowsAffected)
    if (countRowsAffected > 0) {
      wsSendAlert(wsClients, countRowsAffected);
    }
    res.json({ SUCCESS: true, MESSAGE: "" });
  } catch (error) {
    console.log(error);
    return res.status(400).json({ SUCCESS: false, MESSAGE: "Error al insertar las propuestas" })
  }
}





export const deletePropuestasPagadas = async (req, res) => {//ELIMINA LAS PROPUESTAS PAGADAS
  const connection = await pool;
  const transaction = new sql.Transaction(connection);
  try {
    const { listClavesControl } = req.body;
    if (listClavesControl == null || listClavesControl.length == 0) return res.status(201).json({ SUCCESS: false, MESSAGE: "Nada para actualizar en deletePropuestasPagadas" })

    const stgClavesControlHolders = listClavesControl.map(() => "?").join(",");
    const query = `
      DELETE FROM propuestas WHERE cve_control in(${stgClavesControlHolders})
    `;

    transaction.begin();
    const response = await queryWithParams(connection, query, params, "deletePropuestasPagadas");
    transaction.commit();

    // console.log(response)
    const rowsAffected = parseInt(response.rowsAffected[0]) || 0;
    res.json({ SUCCESS: true, MESSAGE: "", AFFECTED_ROWS: rowsAffected });
  } catch (error) {
    transaction.rollback();
    console.log(error);
    return res.status(400).json({ SUCCESS: false, MESSAGE: "Error al eliminar las propuestas pagadas" })
  }
}

//ELIMINA LAS PROPUESTAS QUE SE ENCUENTREN SIN AUTORIZACION QUE ANTERIORMENTE YA TENIAN ALMENOS LA PRIMERA AUTORIZACION
export const deletePropuestasSinAutorizacion = async (req, res) => {
  const connection = await pool;
  const transaction = new sql.Transaction(connection);
  try {
    const { listClavesControl } = req.body;
    if (listClavesControl == null || listClavesControl.length == 0) return res.status(201).json({ SUCCESS: false, MESSAGE: "Nada para actualizar en deletePropuestasSinAutorizacion" })

    const stgClavesControlHolders = listClavesControl.map(() => "?").join(",");
    const query = `
      DELETE FROM propuestas WHERE cve_control in(${stgClavesControlHolders})
    `;

    transaction.begin();
    const response = await queryWithParams(connection, query, params, "deletePropuestasSinAutorizacion");
    transaction.commit();

    // console.log(response)
    const rowsAffected = parseInt(response.rowsAffected[0]) || 0;
    res.json({ SUCCESS: true, MESSAGE: "", AFFECTED_ROWS: rowsAffected });
  } catch (error) {
    transaction.rollback();
    console.log(error);
    return res.status(400).json({ SUCCESS: false, MESSAGE: "Error al eliminar las propuestas sin autorizacion" })
  }
}

// export const getPropuestasAutorizadasDesautorizadas = async (req, res) => {//OBTIENE LAS PROPUESTAS AUTORIZADAS O DESAUTORIZADAS DESDE LA APP MOVIL
export const getPropuestasModificadasMovil = async (req, res) => {//OBTIENE LAS PROPUESTAS AUTORIZADAS O DESAUTORIZADAS DESDE LA APP MOVIL
  const connection = await pool;
  try {
    const query = `
      SELECT 
      cve_control,usuario_uno,usuario_dos,usuario_tres,motivo_rechazo
      FROM propuestas
      WHERE status = 1
    `;
    // printQuery("getPropuestasModificadasMovil", query);
    const response = await connection.query(query);

    const data = response.recordset.length > 0 ? JSON.stringify(response.recordset) : null
    if (data != null) await resetPropuestasMovil();//TODO ver si las actualizo meidante una respuesta de websocket una ves que estoy seguro que se modificaron en el set
    // Respuesta exitosa
    res.json({
      SUCCESS: true,
      MESSAGE: "",
      DATA: data
    });

  } catch (error) {
    console.error("Error en getPropuestasModificadasMovil:", error);
    return res.status(500).json({ SUCCESS: false, MESSAGE: "Error en el servidor" });
  }
}

const resetPropuestasMovil = async function () {
  const connection = await pool;
  try {
    await connection.query("UPDATE propuestas SET status = 0 WHERE status = 1")
  } catch (error) {
    console.error(error)
  }
}










//TODO ver si se llega a usar
export const operacionesSet = async (req, res) => {
  try {
    const connection = await pool;
    const { propuestas, listClavesControl } = req.body;

    connection.beginTransaction()

    //1 - ACTUALIZA Ó INSERTA CAMPOS________________________________________________________________________________-
    const values = propuestas.map(() => '(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)').join(',\n');

    const query = `
      MERGE INTO propuestas AS target
      USING (VALUES
        ${values}
      ) AS source (cve_control, fec_propuesta, importe, id_divisa, concepto, no_empresa, desc_empresa, id_banco, desc_banco, id_chequera,nivel_autorizacion, usuario_uno, usuario_dos,usuario_tres,motivo_rechazo)
      ON target.cve_control = source.cve_control
      WHEN MATCHED AND(
        ISNULL(source.fec_propuesta,'') <> ISNULL(target.fec_propuesta,'')
        OR ISNULL(source.importe,'') <> ISNULL(target.importe,'')
        OR ISNULL(source.id_divisa,'') <> ISNULL(target.id_divisa,'')
        OR ISNULL(source.concepto,'') <> ISNULL(target.concepto,'')
        OR ISNULL(source.no_empresa,'') <> ISNULL(target.no_empresa,'')
        OR ISNULL(source.desc_empresa,'') <> ISNULL(target.desc_empresa,'')
        OR ISNULL(source.id_banco,'') <> ISNULL(target.id_banco,'')
        OR ISNULL(source.desc_banco,'') <> ISNULL(target.desc_banco,'')
        OR ISNULL(source.id_chequera,'') <> ISNULL(target.id_chequera,'')
        OR ISNULL(source.nivel_autorizacion,'') <> ISNULL(target.nivel_autorizacion,'')
        OR ISNULL(source.usuario_uno,'') <> ISNULL(target.usuario_uno,'')
        OR ISNULL(source.usuario_dos,'') <> ISNULL(target.usuario_dos,'')
        OR ISNULL(source.usuario_tres,'') <> ISNULL(target.usuario_tres,'')
        OR ISNULL(source.motivo_rechazo,'') <> ISNULL(target.motivo_rechazo,'')
      ) THEN UPDATE SET
          fec_propuesta = CASE WHEN ISNULL(source.fec_propuesta,'') <> ISNULL(target.fec_propuesta,'') THEN source.fec_propuesta ELSE target.fec_propuesta END,
          importe = CASE WHEN ISNULL(source.importe,'') <> ISNULL(target.importe,'') THEN source.importe ELSE target.importe END,
          id_divisa = CASE WHEN ISNULL(source.id_divisa,'') <> ISNULL(target.id_divisa,'') THEN source.id_divisa ELSE target.id_divisa END,
          concepto = CASE WHEN ISNULL(source.concepto,'') <> ISNULL(target.concepto,'') THEN source.concepto ELSE target.concepto END,
          no_empresa = CASE WHEN ISNULL(source.no_empresa,'') <> ISNULL(target.no_empresa,'') THEN source.no_empresa ELSE target.no_empresa END,
          desc_empresa = CASE WHEN ISNULL(source.desc_empresa,'') <> ISNULL(target.desc_empresa,'') THEN source.desc_empresa ELSE target.desc_empresa END,
          id_banco = CASE WHEN ISNULL(source.id_banco,'') <> ISNULL(target.id_banco,'') THEN source.id_banco ELSE target.id_banco END,
          desc_banco = CASE WHEN ISNULL(source.desc_banco,'') <> ISNULL(target.desc_banco,'') THEN source.desc_banco ELSE target.desc_banco END,
          id_chequera = CASE WHEN ISNULL(source.id_chequera,'') <> ISNULL(target.id_chequera,'') THEN source.id_chequera ELSE target.id_chequera END,
          nivel_autorizacion = CASE WHEN ISNULL(source.nivel_autorizacion,'') <> ISNULL(target.nivel_autorizacion,'') THEN source.nivel_autorizacion ELSE target.nivel_autorizacion END,
          usuario_uno = CASE WHEN ISNULL(source.usuario_uno,'') <> ISNULL(target.usuario_uno,'') THEN source.usuario_uno ELSE target.usuario_uno END,
          usuario_dos = CASE WHEN ISNULL(source.usuario_dos,'') <> ISNULL(target.usuario_dos,'') THEN source.usuario_dos ELSE target.usuario_dos END,
          usuario_tres = CASE WHEN ISNULL(source.usuario_tres,'') <> ISNULL(target.usuario_tres,'') THEN source.usuario_tres ELSE target.usuario_tres END,
          motivo_rechazo = CASE WHEN ISNULL(source.motivo_rechazo,'') <> ISNULL(target.motivo_rechazo,'') THEN source.motivo_rechazo ELSE target.motivo_rechazo END
      WHEN NOT MATCHED THEN
        INSERT (cve_control, fec_propuesta, importe, id_divisa, concepto, no_empresa, desc_empresa, id_banco, desc_banco, id_chequera,nivel_autorizacion, usuario_uno, usuario_dos,usuario_tres,motivo_rechazo)
        VALUES (source.cve_control, source.fec_propuesta, source.importe, source.id_divisa, source.concepto, source.no_empresa, source.desc_empresa, source.id_banco, source.desc_banco, source.id_chequera,source.nivel_autorizacion, source.usuario_uno, source.usuario_dos,source.usuario_tres,source.motivo_rechazo);
    `;

    const params = propuestas.flatMap(propuesta => [//todos los subarrays los deja en uno solo
      propuesta.cve_control,
      formatDB(propuesta.fec_propuesta),
      propuesta.importe, propuesta.id_divisa, propuesta.concepto, propuesta.no_empresa, propuesta.desc_empresa, propuesta.id_banco, propuesta.desc_banco, propuesta.id_chequera, propuesta.nivel_autorizacion, propuesta.usuario_uno, propuesta.usuario_dos, propuesta.usuario_tres, propuesta.motivo_rechazo
    ]);

    const response = await queryWithParams(connection, query, params, "updatePropuestas");
    // console.log(response, "END update propuestas_____________________________\n")
    const rowsAffected = response.rowsAffected;
    console.log("cambios propuestas: ", rowsAffected)
    if (rowsAffected > 0) {
      wsSendAlert(wsClients, rowsAffected);
    }

    //TODO por migrar a sql server
    //2 - ELIMINAR PROPUESTAS PAGADAS________________________________________________________________
    const stgClavesControlHolders = listClavesControl.map(() => "?").join(",");
    query = `
      DELETE FROM propuestas WHERE cve_control in(${stgClavesControlHolders})
    `;

    printQuery(query, listClavesControl)

    const [response2] = await connection.query(query, listClavesControl);

    // console.log(response)
    // res.json({ SUCCESS: true, MESSAGE: "", AFFECTED_ROWS: response2.affectedRows });
    // res.json({ SUCCESS: true, MESSAGE: "" });

    //3 - getPropuestasModificadas_____________________________________________________________________
    query = `
        SELECT 
        cve_control,usuario_dos,usuario_tres
        FROM propuestas
        WHERE status = 1
      `;
    printQuery("getPropuestasModificadasMovil", query);
    const [rows] = await connection.query(query);

    const data = rows.length > 0 ? rows : null


    res.json({
      SUCCESS: true,
      MESSAGE: "",
      DATA: data
    });
    connection.commit();
  } catch (error) {
    connection.rollback();
    console.log(error);
    return res.status(400).json({ SUCCESS: false, MESSAGE: "Error en operacionesSet", DATA: null })
  }
}





