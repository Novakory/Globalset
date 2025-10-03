
import { generateError } from '../utils/errorsUtil.js';
import { pool } from '../bd.js';
import { query, response } from 'express';
import { printQuery, encriptador } from '../utils/dbUtil.js';
import { formatDB } from '../utils/dateUtil.js';
import { wsClients } from '../utils/wsServer.js'
import { wsSendAlert } from '../utils/wsService.js'

export const operacionesSet = async (req, res) => {
  const connection = await pool.getConnection();
  try {
    const { propuestas, listClavesControl } = req.body;

    connection.beginTransaction()

    //1 - ACTUALIZA Ó INSERTA CAMPOS________________________________________________________________________________-
    const values = propuestas.map(() => '(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)').join(',\n');

    const query = `
      INSERT INTO propuestas (cve_control, fec_propuesta, importe, id_divisa, concepto, no_empresa, desc_empresa, id_banco, desc_banco, id_chequera,nivel_autorizacion, usuario_uno, usuario_dos,usuario_tres)
      VALUES 
      ${values}
      ON DUPLICATE KEY UPDATE WHEN status IS NOT NULL
        fec_propuesta = IF(VALUES(fec_propuesta) != fec_propuesta, VALUES(fec_propuesta), fec_propuesta),
        importe = IF(VALUES(importe) != importe, VALUES(importe), importe),
        id_divisa = IF(VALUES(id_divisa) != id_divisa, VALUES(id_divisa), id_divisa),
        concepto = IF(VALUES(concepto) != concepto, VALUES(concepto), concepto),
        no_empresa = IF(VALUES(no_empresa) != no_empresa, VALUES(no_empresa), no_empresa),
        desc_empresa = IF(VALUES(desc_empresa) != desc_empresa, VALUES(desc_empresa), desc_empresa),
        id_banco = IF(VALUES(id_banco) != id_banco, VALUES(id_banco), id_banco),
        desc_banco = IF(VALUES(desc_banco) != desc_banco, VALUES(desc_banco), desc_banco),
        id_chequera = IF(VALUES(id_chequera) != id_chequera, VALUES(id_chequera), id_chequera),
        nivel_autorizacion = IF(VALUES(nivel_autorizacion) != nivel_autorizacion, VALUES(nivel_autorizacion), nivel_autorizacion),
        usuario_uno = IF(VALUES(usuario_uno) != usuario_uno, VALUES(usuario_uno), usuario_uno),
        usuario_dos = IF(VALUES(usuario_dos) != usuario_dos, VALUES(usuario_dos), usuario_dos),
        usuario_tres = IF(VALUES(usuario_tres) != usuario_tres, VALUES(usuario_tres), usuario_tres);
    `;

    const params = propuestas.flatMap(propuesta => [//todos los subarrays los deja en uno solo
      propuesta.cve_control,
      formatDB(propuesta.fec_propuesta),
      propuesta.importe, propuesta.id_divisa, propuesta.concepto, propuesta.no_empresa, propuesta.desc_empresa, propuesta.id_banco, propuesta.desc_banco, propuesta.id_chequera, propuesta.nivel_autorizacion, propuesta.usuario_uno, propuesta.usuario_dos, propuesta.usuario_tres
    ]);
    // printQuery("updatePropuestas", query, params);
    const [response] = await connection.query(query, params);
    console.log(response, "END update propuestas_____________________________\n")


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

export const updatePropuestas = async (req, res) => {//ACTUALIA CON LAS PROPUESTAS DEL SET
  try {
    const { propuestas } = req.body;
    // console.log({ propuestas });
    if(propuestas==null || propuestas.length==0)  return res.status(201).json({ SUCCESS: false, MESSAGE: "Nada para actualizar en updatePropuestas" }) 
    const values = propuestas.map(() => '(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)').join(',\n');

    const query = `
      INSERT INTO propuestas (cve_control, fec_propuesta, importe, id_divisa, concepto, no_empresa, desc_empresa, id_banco, desc_banco, id_chequera,nivel_autorizacion, usuario_uno, usuario_dos,usuario_tres)
      VALUES 
      ${values}
      ON DUPLICATE KEY UPDATE 
        fec_propuesta = IF(IFNULL(VALUES(fec_propuesta),'') != IFNULL(fec_propuesta,''), VALUES(fec_propuesta), fec_propuesta),
        importe = IF(IFNULL(VALUES(importe),'') != IFNULL(importe,''), VALUES(importe), importe),
        id_divisa = IF(IFNULL(VALUES(id_divisa),'') != IFNULL(id_divisa,''), VALUES(id_divisa), id_divisa),
        concepto = IF(IFNULL(VALUES(concepto),'') != IFNULL(concepto,''), VALUES(concepto), concepto),
        no_empresa = IF(IFNULL(VALUES(no_empresa),'') != IFNULL(no_empresa,''), VALUES(no_empresa), no_empresa),
        desc_empresa = IF(IFNULL(VALUES(desc_empresa),'') != IFNULL(desc_empresa,''), VALUES(desc_empresa), desc_empresa),
        id_banco = IF(IFNULL(VALUES(id_banco),'') != IFNULL(id_banco,''), VALUES(id_banco), id_banco),
        desc_banco = IF(IFNULL(VALUES(desc_banco),'') != IFNULL(desc_banco,''), VALUES(desc_banco), desc_banco),
        id_chequera = IF(IFNULL(VALUES(id_chequera),'') != IFNULL(id_chequera,''), VALUES(id_chequera), id_chequera),
        nivel_autorizacion = IF(IFNULL(VALUES(nivel_autorizacion),'') != IFNULL(nivel_autorizacion,''), VALUES(nivel_autorizacion), nivel_autorizacion),
        usuario_uno = IF(IFNULL(VALUES(usuario_uno),'') != IFNULL(usuario_uno,''), VALUES(usuario_uno), usuario_uno),
        usuario_dos = IF(IFNULL(VALUES(usuario_dos),'') != IFNULL(usuario_dos,''), VALUES(usuario_dos), usuario_dos),
        usuario_tres = IF(IFNULL(VALUES(usuario_tres),'') != IFNULL(usuario_tres,''), VALUES(usuario_tres), usuario_tres);
    `;

    const params = propuestas.flatMap(propuesta => [//todos los subarrays los deja en uno solo
      propuesta.cve_control,
      formatDB(propuesta.fec_propuesta),
      propuesta.importe, propuesta.id_divisa, propuesta.concepto, propuesta.no_empresa, propuesta.desc_empresa, propuesta.id_banco, propuesta.desc_banco, propuesta.id_chequera, propuesta.nivel_autorizacion, propuesta.usuario_uno, propuesta.usuario_dos, propuesta.usuario_tres
    ]);
    // printQuery("updatePropuestas", query, params);
    const [response] = await pool.query(query, params);
    // console.log(response, "END update propuestas_____________________________\n")
    const rowsAffected = parseInt(response.affectedRows) - propuestas.length;
    console.log("cambios propuestas: ", rowsAffected)
    if (rowsAffected > 0) {
      wsSendAlert(wsClients, rowsAffected);
    }
    res.json({ SUCCESS: true, MESSAGE: "" });
  } catch (error) {
    console.log(error);
    return res.status(400).json({ SUCCESS: false, MESSAGE: "Error al insertar las propuestas" })
  }
}
export const deletePropuestasPagadas = async (req, res) => {//ELIMINA LAS PROPUESTAS PAGADAS
  const connection = await pool.getConnection(); //la conexion se usa para poder trabajar con transacciones
  try {
    const { listClavesControl } = req.body;
    if(listClavesControl==null || listClavesControl.length==0)  return res.status(201).json({ SUCCESS: false, MESSAGE: "Nada para actualizar en deletePropuestasPagadas" }) 

    const stgClavesControlHolders = listClavesControl.map(() => "?").join(",");
    const query = `
      DELETE FROM propuestas WHERE cve_control in(${stgClavesControlHolders})
    `;

    // printQuery(query, listClavesControl)

    connection.beginTransaction()
    const [response] = await connection.query(query, listClavesControl);
    connection.commit();

    // console.log(response)
    res.json({ SUCCESS: true, MESSAGE: "", AFFECTED_ROWS: response.affectedRows });
  } catch (error) {
    connection.rollback();
    console.log(error);
    return res.status(400).json({ SUCCESS: false, MESSAGE: "Error al eliminar las propuestas pagadas" })
  } finally {
    connection.release();
  }
}

// export const getPropuestasAutorizadasDesautorizadas = async (req, res) => {//OBTIENE LAS PROPUESTAS AUTORIZADAS O DESAUTORIZADAS DESDE LA APP MOVIL
export const getPropuestasModificadasMovil = async (req, res) => {//OBTIENE LAS PROPUESTAS AUTORIZADAS O DESAUTORIZADAS DESDE LA APP MOVIL
  try {
    const query = `
      SELECT 
      cve_control,usuario_dos,usuario_tres
      FROM propuestas
      WHERE status = 1
    `;
    // printQuery("getPropuestasModificadasMovil", query);
    const [rows] = await pool.query(query);

    const data = rows.length > 0 ? JSON.stringify(rows) : null
    if (data != null) await updateStatusPropuestasModificasMovil();//TODO ver si las actualizo meidante una respuesta de websocket una ves que estoy seguro que se modificaron en el set
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

const updateStatusPropuestasModificasMovil = async function () {
  await pool.query("UPDATE propuestas SET status = 0 WHERE status = 1")
}