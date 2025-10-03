
// import { WebSocketServer } from "ws";
// const server = createServer(app);
// const wss = new WebSocketServer({ server }); // Vincula el servidor HTTP al WebSocket Server
// // const wss = new WebSocketServer({ port: 3002 }); // Vincula el servidor HTTP al WebSocket Server

// // Manejar conexiones de WebSocket
// wss.on('connection', (ws) => {
//   console.log('Cliente conectado');

//   // Manejar mensajes recibidos del cliente
//   ws.on('message', (message) => {
//     console.log('Mensaje recibido:', message.toString());

//     // Responder al cliente
//     ws.send(`Mensaje recibido: ${message}`);
//   });

//   // Manejar desconexiones
//   ws.on('close', () => {
//     console.log('Cliente desconectado');
//   });
// });

