
import "dotenv/config";
import express from "express";
import cors from "cors";
import { createServer } from "http";
import routes from './routes/routes.js';
import { wss } from './utils/wsServer.js'

const app = express();

//no se ocupa el cors en produccion si el frontend permanece en el mismo servidor que el backend
const whiteList = [process.env.FRONTEND_URL, "https://websocketking.com"]
if (process.argv[2] == '--developer') whiteList.push(undefined);//paa que en modo test pueda hacer peticiones desde postman
const corsOption = {
  origin: function (origin, callback) {
    console.log({ origin });
    if (whiteList.includes(origin)) {
      callback(null, true);
    } else {
      callback(new Error("Error en CORS"));
    }
  }
}

app.use(cors(corsOption))
app.use(express.urlencoded({ extended: true })); // Para manejar datos URL-encoded
app.use(express.json());//para poder resibir datos de tipo JSON en las peticiones
app.use('/api-v1', routes);

const server = createServer(app);
server.on('upgrade', (request, socket, head) => {
  wss.handleUpgrade(request, socket, head, (ws) => {
    wss.emit('connection', ws, request); // Emite la conexión
  });
});


const PORT = process.env.PORT || 3000;

server.listen(PORT, () => {
  console.log("http://localhost:" + PORT);
})
