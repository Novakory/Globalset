import sql from 'mssql';

const dbSettings = {
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  server: process.env.DB_HOST,
  port: parseInt(process.env.DB_PORT) || 1433,
  database: process.env.DB_NAME,
  options: {
    encrypt: false, // true si usas Azure
    trustServerCertificate: true // necesario en local
  }
};

// Crear pool de conexiones
export const pool = new sql.ConnectionPool(dbSettings)
  .connect()
  .then(pool => {
    console.log('✅ Conectado a SQL Server');
    return pool;
  })
  .catch(err => {
    console.error('❌ Error de conexión a SQL Server:', err);
    throw err;
  });
