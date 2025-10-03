import { parse, format, isValid } from "date-fns";

/**
 * Devuelve la fecha introducida en formato Date
 * @param {String} sDate
 * @returns {Date}
 */

export function toDate(sDate) {
  // console.log(sDate);
  const patrones = ['dd/MM/yyyy', 'yyyy/MM/dd', 'MM/dd/yyyy', 'dd-MM-yyyy', 'yyyy-MM-dd', 'MM-dd-yyyy', 'MM/dd/yy', 'MM-dd-yy'];

  for (let patron of patrones) {
    try {
      // console.log(patron)
      const dfecha = parse(sDate, patron, new Date());
      // console.log(dfecha);
      if (isValid(dfecha)) return dfecha;
    } catch (error) {
      // Ignoramos el error y seguimos intentando con el siguiente formato
    }
  }

  return null;
}
/**
 * Devuelve la fecha introducida en formato Date
 * @param {String} sDate
 * @returns {Date}
 */
export function getNextPeriod(date) {
  if (typeof date === 'string') date = toDate(date);
  date = new Date(date.getFullYear(), date.getMonth() + 1, 1);
  return date.getMonth() + "/" + date.getFullYear();
}
/**
 * Devuelve la fecha introducida en formato yyyy-mm-dd
 * @param {Date | String} date
 * @returns {String} - en fomato yyyy-mm-dd
 */
export function formatDB(date) {
  // const opciones = { year: 'numeric', month: '2-digit', day: '2-digit' };
  // const dateStg = date.toLocaleDateString('es-ES', opciones);
  // console.log(fechaFormateada);  // Ejemplo de salida: 19/09/2024
  if (typeof date === 'string') date = toDate(date);
  return date.toLocaleDateString('en-CA'); // 'en-CA' devuelve el formato yyyy-MM-dd
}
export function getMinDate(date) {
  try {
    if (typeof date === 'string') date = toDate(date);
    date = new Date(date.getFullYear(), date.getMonth(), 1);
    console.log('getMinDate', date);
    return formatDB(date);
  } catch (error) {
    console.log(error);
    return null;
  }
}