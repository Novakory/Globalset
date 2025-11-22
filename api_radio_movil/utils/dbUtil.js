import crypto from 'crypto'
import sql from 'mssql'

export async function queryWithParams(connection, query, params, method) {
  const request = connection.request();

  // Reemplazamos cada '?' por '@p0', '@p1', ...
  let i = 0;
  const queryWithNamed = query.replace(/\?/g, () => `@p${i++}`);

  // Agregamos los parámetros al request
  params.forEach((value, index) => {
    request.input(`p${index}`, typeof value === "number" ? sql.Int : sql.VarChar, value);
  });

  if(!(method === "" || method === undefined || method === null)) printQuery(method,query,params)

  return request.query(queryWithNamed);
}


export function printQuery(method, baseQuery, params = null) {
  // console.log("baseQuery: ",baseQuery)
  // console.log("params: ",params)
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