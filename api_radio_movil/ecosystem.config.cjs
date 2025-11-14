const path = require('path');
module.exports = {
  apps: [
    {
      name: "api_radio_movil",
      script: path.join(__dirname, "app.js"),
      env: {
        // DB_USER: "sa",
        // DB_PASSWORD: "TnYtx27kNsaLxDxH",
        // DB_USER: "usrapp",
        // DB_PASSWORD: "rgXe25?YTCYb7Mco",

        // DB_HOST: "localhost",
        // PORT: "3002",
        // DB_PORT: 1433,
        // DB_NAME: "radio_movil",
        // JWT_SECRET: "radio_movil"
        // FRONTEND_URL: "ok"

        PORT: "3002",
        DB_USER: "sa",
        DB_PASSWORD: "Supern0va",
        DB_PORT: 1433,
        DB_HOST: "localhost",
        DB_NAME: "radio_movil",
        JWT_SECRET: "radio_movil",
        FRONTEND_URL: "ok"
      }
    }
  ]
};
