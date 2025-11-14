import jwt from "jsonwebtoken";
import { generateError } from './errorsUtil.js'
import "dotenv/config";

const SECRET_KEY = process.env.JWT_SECRET || "clave_secreta_segura"; // define en .env en producción

// Generar token
export function generateToken(payload) {
    return jwt.sign(payload, SECRET_KEY, { expiresIn: "1h" });
}

// Verificar token
export function verifyToken(token) {
    try {
        return jwt.verify(token, SECRET_KEY); // si es válido, devuelve el payload decodificado
    } catch (error) {
        // Personalizamos según el tipo de error
        if (error.name === "TokenExpiredError") {
            throw generateError(403, "El token ha expirado. Inicia sesión nuevamente.");
        } else if (error.name === "JsonWebTokenError") {
            throw generateError(403, "El token es inválido o ha sido manipulado.");
        } else if (error.name === "NotBeforeError") {
            throw generateError(403, "El token aún no es válido.");
        } else {
            throw generateError(403, "Error al verificar el token.");
        }
    }
}
