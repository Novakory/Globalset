// import { WS_SEND_CHANGE, WS_SEND_ALERT } from "./c";
export const WS_SEND_ALERT_BY_API = 1;
export const WS_TYPE_REGISTRAR_CLIENTE = 2;
// export const WS_SEND_CHANGE = 2;
export const WS_TYPE_GET_DETALLE_BY_MOVIL = 3
export const WS_TYPE_GET_DETALLE_BY_API = 4
export const WS_TYPE_SEND_DETALLE_BY_SET = 5
export const WS_TYPE_SEND_DETALLE_BY_API = 6

// import {} from "WebSo"
export function wsSendMessage(wsClients) {
  for (const [id, clientWs] of wsClients.entries()) {
    // Verificar que la conexión está abierta antes de intentar enviar un mensaje
    if (clientWs.readyState === WebSocket.OPEN) {
      clientWs.send(`Broadcast: ${message}`);
    }
  }
}
export function wsSendDetalles(wsClients, detallesPropuestas, claveControl, claveUsuario) {
  for (const [id, clientWs] of wsClients.entries()) {
    // Verificar que la conexión está abierta antes de intentar enviar un mensaje
    // if (client.readyState === WebSocket.OPEN) {
    if (id === claveUsuario) {
      const message = JSON.stringify({
        type: WS_TYPE_SEND_DETALLE_BY_API,
        claveControl,
        data: detallesPropuestas,
        // message: "Manda el detalle de la propuesta a la app movil"
      })
      clientWs.send(message);
      break;
    }
    // }
  }
}
export function wsSendGetDetalles(wsClients, cveControl, claveUsuario) {
  for (const [id, clientWs] of wsClients.entries()) {
    if (id === "SET") {
      const message = JSON.stringify({
        type: WS_TYPE_GET_DETALLE_BY_API,
        claveControl: cveControl,
        claveUsuario,
        message: "Manda alerta al set para traer el detalle de la propuesta"
      })
      clientWs.send(message);
      break;
    }
  }

  // Verificar que la conexión está abierta antes de intentar enviar un mensaje
  // if (client.readyState === WebSocket.OPEN) {

  // }

  // wsClients.forEach((client) => {
  //   // Verificar que la conexión está abierta antes de intentar enviar un mensaje
  //   // if (client.readyState === WebSocket.OPEN) {
  //   const message = JSON.stringify({
  //     type: WS_TYPE_GET_DETALLE_BY_API,
  //     claveControl:cveControl,
  //     message: "Manda alerta al set para traer el detalle de la propuesta"
  //   })
  //   client.send(message);
  //   // }
  // });
}
export function wsSendAlert(wsClients, numChanges) {
  for (const [id, clientWs] of wsClients.entries()) {
    // Verificar que la conexión está abierta antes de intentar enviar un mensaje
    // if (client.readyState === WebSocket.OPEN) {
    const message = JSON.stringify({
      type: WS_SEND_ALERT_BY_API,
      numChanges,
      message: "Manda alerta a los dispositivos moviles conectados para avisar que hay nuevos cambios"
    })
    clientWs.send(message);
    // }
  }
}
// export function wsSendChange(wsClients) {
//   for (const [id, clientWs] of wsClients.entries()) {
//     // Verificar que la conexión está abierta antes de intentar enviar un mensaje
//     if (clientWs.readyState === WebSocket.OPEN) {
//       clientWs.send({
//         type: WS_SEND_CHANGE,
//         message: "newChange"//TODO
//       });
//     }
//   }
// }