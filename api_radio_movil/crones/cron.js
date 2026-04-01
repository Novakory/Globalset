import cron from 'node-cron'
import { eliminaPropuestasAntiguas } from '../controllers/controllerPorpuestasMovil.js'

cron.schedule('0 10 * * *', async () => {
    console.log('Ejecutando cron');
    await eliminaPropuestasAntiguas();
});