import { WebSocketServer } from "ws";
import {
  wsSendGetDetalles, wsSendDetalles,
  WS_TYPE_REGISTRAR_CLIENTE, WS_TYPE_GET_DETALLE_BY_MOVIL, WS_TYPE_SEND_DETALLE_BY_SET
} from "./wsService.js"

const wss = new WebSocketServer({ noServer: true });

// Almacenar las conexiones WebSocket activas
const wsClients = new Map();
// const wsClients = new Set();

// Manejar nuevas conexiones WebSocket
wss.on('connection', (ws) => {
  

  console.log('Nuevo cliente conectado');

  // Agregar la conexión al conjunto de clientes
  // wsClients.add(ws);

  // Escuchar los mensajes enviados por el cliente
  ws.on('message', (message) => {
    console.log(`Mensaje recibido: ${message}`);
    let obj = null;
    try {
      obj = JSON.parse(message);
    } catch (e) {

    }

    if (obj?.type === WS_TYPE_REGISTRAR_CLIENTE) {
      wsClients.set(
        obj.id, ws
      );
      console.log("numClientesActuales:", wsClients.size)
    } else if (obj?.type === WS_TYPE_GET_DETALLE_BY_MOVIL) {
      console.log("LLAMAR AVISO AL SET")
      wsSendGetDetalles(wsClients, obj.claveControl, obj.claveUsuario)
    } else if (obj?.type === WS_TYPE_SEND_DETALLE_BY_SET) {
      console.log("MANDAR LOS DATOS AL MOVIL")
      wsSendDetalles(wsClients, obj.detalle, obj.claveControl, obj.claveUsuario)
    }
  });

  // Manejar la desconexión
  ws.on('close', () => {
    for (const [id, clientWs] of wsClients.entries()) {
      if (clientWs === ws) {
        wsClients.delete(id); // Eliminar el cliente del mapa
        console.log(`Cliente desconectado con ID: ${id}`);
        break;
      }
    }
    // console.log('Cliente desconectado');
    // wsClients.delete(ws); // Eliminar del conjunto de clientes
    console.log("numClientesActuales:", wsClients.size)
  });
});

// Exportar el servidor WebSocket y la lista de clientes
export {
  wss,
  wsClients
}
