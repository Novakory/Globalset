import crypto from 'crypto';
export function printQuery(method, baseQuery, params = null) {
  if (Array.isArray(params)) {
    params.forEach(param => {
      baseQuery = baseQuery.replace("?", param)
    });
  } else if (params != null) {
    baseQuery = baseQuery.replace("?", params)
  }
  // console.log(baseQuery);
  console.log(`query ${method}: ${baseQuery}`);
}

export function filterParams(queryParts) {
  try {
    const { columns, params } = queryParts;

    // Filtrar columnas y valores que no son undefined o null
    const filteredColumns = columns.filter((col, index) => params[index] !== undefined && params[index] !== null);
    const filteredParams = params.filter(param => param !== undefined && param !== null);

    // Construir la parte SET de la consulta
    const clause = filteredColumns.map(col => `${col} = ?`).join(", ");
    return {
      filteredColumns, filteredParams, clause
    }
  } catch (error) {
    console.log(error);
  }
}

export function getClause(queryParts) {
  try {
    const { columns, params } = queryParts;

    // Filtrar columnas y valores que no son undefined o null
    const filteredColumns = columns.filter((col, index) => params[index] !== undefined);
    const filteredParams = params.filter(param => param !== undefined);

    // Construir la parte SET de la consulta
    const clause = filteredColumns.map(col => `${col} = ?`).join(", ");
    return {
      filteredColumns, filteredParams, clause
    }
  } catch (error) {
    console.log(error);
  }
}

export function encriptador(password) {
  try {
    // Crear un hash SHA-256
    const hash = crypto.createHash('sha256');
    hash.update(password); // Añadir la contraseña al hash
    const sPassword = hash.digest('hex'); // Obtener el hash en formato hexadecimal
    return sPassword;
  } catch (error) {
    console.error("Error al encriptar:", error);
    return null;
  }
}