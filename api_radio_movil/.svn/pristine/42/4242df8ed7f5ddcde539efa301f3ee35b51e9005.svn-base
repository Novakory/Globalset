function generateError(message, res, status) {//https://developer.mozilla.org/es/docs/Web/HTTP/Status/401
  const error = new Error(message);
  return res.status(status).json({//404 es para cuando no se encuentra algun dato en la bd
    msg: error.message
  })
}

export {
  generateError
}