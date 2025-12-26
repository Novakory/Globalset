
import { generateError, getGenericError } from '../utils/errorsUtil.js';
import { pool } from '../bd.js';
import sql from 'mssql'
import { query, response } from 'express';
import { printQuery, encriptador, queryWithParams } from '../utils/dbUtil.js';
import { generateToken } from '../utils/jwt.js'
import "../utils/logger.js";

export const validaLogin = async (req, res) => {//ok
  const connection = await pool;
  try {
    const { user, password } = req.body;

    // Validar datos de entrada
    if (!user || !password) {
      throw generateError(400, "Usuario y contraseña son obligatorios")
    }

    // Encriptar contraseña
    const passwordEncriptado = encriptador(password);

    // Consulta SQL
    // const query = `SELECT facultad_acceso,id_usuario FROM usuarios WHERE clave_usuario = @clave_usuario AND contrasena = @contrasena`;
    const query = `SELECT * FROM usuarios WHERE clave_usuario = ? AND contrasena = ?`;

    const result = await queryWithParams(connection, query, [user, passwordEncriptado], "validaLogin")
    // printQuery("validaLogin", query, [user, passwordEncriptado]);
    // console.log("query: ",query)+
    // const [rows] = await pool.query(query, [user, passwordEncriptado]);
    // const result = await connection.request()
    //   .input("clave_usuario", sql.TYPES.VarChar, user)
    //   .input("contrasena", sql.TYPES.VarChar, passwordEncriptado)
    //   .query(query)

    console.log("RESULT QUERY: ", result)
    // 
    // Validar resultado
    if (result.recordset.length === 0) {
      throw generateError(401, "Credenciales incorrectas")
    }
    if (result.recordset[0].facultad_acceso === 0) {
      throw generateError(401, "tienes facultad de acceso");
    }

    // const usuario = { ...result.recordset[0], contrasena: '' }
    const { contrasena, empresas, ...usuario } = result.recordset[0];



    const token = generateToken({
      id_usuario: usuario.id_usuario
    });
    res.json({ SUCCESS: true, MESSAGE: "", USUARIO: usuario, TOKEN: token });

  } catch (error) {
    console.error("Error en validaLogin:", error);
    return getGenericError(res, error);
    // return res.status(500).json({ SUCCESS: false, MESSAGE: "Error en el servidor", USUARIO: null });
  }
}