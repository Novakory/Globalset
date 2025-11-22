// function generateError(message, res, status) {//https://developer.mozilla.org/es/docs/Web/HTTP/Status/401
//   const error = new Error(message);
//   return res.status(status).json({//404 es para cuando no se encuentra algun dato en la bd
//     msg: error.message
//   })
// }

function generateError(status, message) {
  let objError = new Error(message)
  objError.MESSAGE = message
  objError.status = status
  return objError
}
function getGenericError(res,error) {
  const message = error.MESSAGE || error.message || JSON.stringify(error)
  const status = error.status || 500;
  return res.status(status).json({ SUCCESS: false, MESSAGE: message });
}
export {
  generateError,
  getGenericError
}