
import { generateError } from '../utils/errorsUtil.js';
import { pool } from '../bd.js';
import { queryWithParams } from '../utils/dbUtil.js';

//TODO - SI LLEGA A HABER DEMASIADOS USUARIOS PUEDE MARCAR ERRROR POR LOS MAXIMOS 2100 PARAMETROS POR OPERACION
//EN ESE CASO REMPLAZAR POR LA FORMA DE BATCHS COMO EN controlllerPropuestasSet.js
export const updateUsers = async (req, res) => {//ok
  try {
    const connection = await pool;

    const { users } = req.body;
    if (users == null || users.length == 0) return res.status(201).json({ SUCCESS: false, MESSAGE: "Nada para actualizar en updateUsers" })

    const values = users.map(() => '(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)').join(',\n');

    const query = `
      MERGE INTO usuarios AS target
      USING (VALUES
        ${values}
      ) AS source (id_usuario, nombre, apellido_materno, apellido_paterno, clave_usuario, contrasena, empresas, facultad_acceso,facultad_mancomunada,facultad_total,facultad_rechazar, monto_maximo_pagar)
      ON target.id_usuario = source.id_usuario
      WHEN MATCHED AND(
        ISNULL(source.contrasena,'') <> ISNULL(target.contrasena,'')
        OR ISNULL(source.empresas,'') <> ISNULL(target.empresas,'')
        OR ISNULL(source.facultad_acceso,'') <> ISNULL(target.facultad_acceso,'')
        OR ISNULL(source.facultad_mancomunada,'') <> ISNULL(target.facultad_mancomunada,'')
        OR ISNULL(source.facultad_total,'') <> ISNULL(target.facultad_total,'')
        OR ISNULL(source.facultad_rechazar,'') <> ISNULL(target.facultad_rechazar,'')
        OR ISNULL(source.monto_maximo_pagar,'') <> ISNULL(target.monto_maximo_pagar,'')
      ) THEN UPDATE SET
          contrasena = CASE WHEN ISNULL(source.contrasena,'') <> ISNULL(target.contrasena,'') THEN source.contrasena ELSE target.contrasena END,
          empresas = CASE WHEN ISNULL(source.empresas,'') <> ISNULL(target.empresas,'') THEN source.empresas ELSE target.empresas END,
          facultad_acceso = CASE WHEN ISNULL(source.facultad_acceso,'') <> ISNULL(target.facultad_acceso,'') THEN source.facultad_acceso ELSE target.facultad_acceso END,
          facultad_mancomunada = CASE WHEN ISNULL(source.facultad_mancomunada,'') <> ISNULL(target.facultad_mancomunada,'') THEN source.facultad_mancomunada ELSE target.facultad_mancomunada END,
          facultad_total = CASE WHEN ISNULL(source.facultad_total,'') <> ISNULL(target.facultad_total,'') THEN source.facultad_total ELSE target.facultad_total END,
          facultad_rechazar = CASE WHEN ISNULL(source.facultad_rechazar,'') <> ISNULL(target.facultad_rechazar,'') THEN source.facultad_rechazar ELSE target.facultad_rechazar END,
          monto_maximo_pagar = CASE WHEN ISNULL(source.monto_maximo_pagar,'') <> ISNULL(target.monto_maximo_pagar,'') THEN source.monto_maximo_pagar ELSE target.monto_maximo_pagar END
      WHEN NOT MATCHED THEN
        INSERT (id_usuario, nombre, apellido_materno, apellido_paterno, clave_usuario, contrasena, empresas, facultad_acceso,facultad_mancomunada,facultad_total,facultad_rechazar, monto_maximo_pagar)
        VALUES (source.id_usuario, source.nombre, source.apellido_materno, source.apellido_paterno, source.clave_usuario, source.contrasena, source.empresas, source.facultad_acceso, source.facultad_mancomunada, source.facultad_total, source.facultad_rechazar, source.monto_maximo_pagar);
    `;

    const params = users.flatMap(user => [//todos los subarrays los deja en uno solo
      user.id_usuario, user.nombre, user.apellido_materno, user.apellido_paterno, user.clave_usuario, user.contrasena, user.empresas, 
      user.facultad_acceso,user.facultad_mancomunada,user.facultad_total, user.facultad_rechazar, user.monto_maximo_pagar
    ]);
    const response = await queryWithParams(connection, query, params);
    
    console.log("cambios usuarios: ", response.rowsAffected)
    res.json({ SUCCESS: true, MESSAGE: "" });
  } catch (error) {
    console.log(error);
    return res.status(400).json({ SUCCESS: false, MESSAGE: "Error al insertar los usuarios" })
  }
}
