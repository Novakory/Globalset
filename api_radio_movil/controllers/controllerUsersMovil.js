
import { generateError } from '../utils/errorsUtil.js';
import { pool } from '../bd.js';
import { sql } from 'mssql';
import { query, response } from 'express';
import { printQuery, encriptador } from '../utils/dbUtil.js';


export const validaLogin = async (req, res) => {
  try {
    const { user, password } = req.body;
    console.log("INTO validaLOgin");

    // Validar datos de entrada
    if (!user || !password) {
      return res.status(400).json({ SUCCESS: false, ERROR_MESSAGE: "Usuario y contraseña son obligatorios" });
      // return res.json({ SUCCESS: false, ERROR_MESSAGE: "Usuario y contraseña son obligatorios" });
    }

    // Encriptar contraseña
    const passwordEncriptado = encriptador(password);

    // Consulta SQL
    const query = `SELECT COUNT(*) AS count, facultad_acceso,id_usuario FROM usuarios WHERE clave_usuario = @clave_usuario AND contrasena = @contrasena`;
    printQuery("validaLogin", query, [user, passwordEncriptado]);
    // const [rows] = await pool.query(query, [user, passwordEncriptado]);
    const result = (await pool).request()
      .input("clave_usuario", sql.VarChar, user)
      .input("contrasena", sql.VarChar, passwordEncriptado)
      .query(query)

    console.log("RESULT QUERY: "+result)

    // Validar resultado
    if (result.recordset[0].count === 0) {
      return res.status(401).json({ SUCCESS: false, ERROR_MESSAGE: "Credenciales incorrectas", ID_USUARIO: 0 });
    }
    if (rows[0].facultad_acceso === 0) {
      return res.status(401).json({ SUCCESS: false, ERROR_MESSAGE: "No tienes facultad de acceso", ID_USUARIO: 0 });
    }

    // Respuesta exitosa
    res.json({ SUCCESS: true, ERROR_MESSAGE: "", ID_USUARIO: rows[0].id_usuario });

  } catch (error) {
    console.error("Error en validaLogin:", error);
    return res.status(500).json({ SUCCESS: false, ERROR_MESSAGE: "Error en el servidor", ID_USUARIO: 0 });
  }
}