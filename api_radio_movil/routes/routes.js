import express from 'express';

import { updateUsers } from '../controllers/controllerUsersSet.js';
import { validaLogin } from '../controllers/controllerUsersMovil.js';

import {
  getPropuestasByUser,
  autorizarPropuestas,
  getPropuestasPendientesByUser,
  desautorizarPropuestas,
  rechazarPropuestas
} from '../controllers/controllerPorpuestasMovil.js';

import { updatePropuestas, deletePropuestasPagadas, getPropuestasModificadasMovil } from '../controllers/controllerPorpuestasSet.js';

//TODO por agregar middlewares
// import { registerUser, verifiyAccount, login, getUser } from '../controllers/authController.js';
import { authMiddleware } from '../middlewares/authMiddleware.js';

const router = express.Router();

//USUARIOS________________________________________________
//SET
router.post('/users', updateUsers);

//MOVIL
router.post('/login', (req, res, next) => {
  // console.log("Headers:", req.headers);
  // console.log("Body:", req.body);
  // console.log("Origin:", req.headers.origin);
  next();
}, validaLogin);

//FIN USUARIOS_______________________________________________-


//PROPUESTAS_______________________________________________________-

//SET
//TODO ver si desde set tambien que sea necesario generar el jwt para mandarlo en la peticion
router.get('/propuestas-modificadas', getPropuestasModificadasMovil);

router.post('/propuestas', updatePropuestas);
router.delete('/propuestas', deletePropuestasPagadas);
// router.get('/propuestas', getPropuestas);

//MOVIL
router.get('/propuestas/user/:clave_usuario', authMiddleware, getPropuestasByUser);
router.get('/propuestas-pendientes/user/:clave_usuario', authMiddleware, getPropuestasPendientesByUser);
router.post('/autorizar-propuestas', authMiddleware, autorizarPropuestas);
router.post('/desautorizar-propuestas', authMiddleware, desautorizarPropuestas);
router.post('/rechazar-propuestas', authMiddleware, rechazarPropuestas);

//FIN PROPUESTAS_____________________________-


// router.post('/test', (req, res, next) => {
//   console.log("Headers:", req.headers);
//   console.log("Body:", req.body);
//   console.log("Origin:", req.headers.origin);
//   next();
// }, test1);



// router.get('/verify/:token', verifiyAccount);
// router.post('/login', login);
// router.get('/user', authMiddleware, getUser)

export default router