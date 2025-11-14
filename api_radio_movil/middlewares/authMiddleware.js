import { verifyToken } from "../utils/jwt.js";
import { getGenericError, generateError } from '../utils/errorsUtil.js'


export function authMiddleware(req, res, next) {
    try {
        // Los tokens suelen venir en el encabezado Authorization: "Bearer <token>"
        const authHeader = req.headers["authorization"];
        const token = authHeader && authHeader.split(" ")[1]; // extrae el token

        if (!token) {
            throw generateError(401, `Token no proporcionado`);
        }

        verifyToken(token)
        next();
    } catch (error) {
        return getGenericError(res, error)
    }
}
