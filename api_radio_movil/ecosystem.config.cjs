const path = require('path');
module.exports = {
  apps: [
    {
      name: "api_radio_movil",
      script: path.join(__dirname, "app.js"),
      env: {
        //PROD
        DB_HOST: "gfdb01",
        DB_PORT: 1433,
        DB_USER: "sa",
        DB_PASSWORD: "Rf@8#w4*3_Uc3h=J",
        DB_NAME: "radio_movil",

        PORT: "3002",
        JWT_SECRET: "radio_movil"
        // FRONTEND_URL: "ok"


        //LOCAL DESKTOP
        // DB_HOST: "DESKTOP-LMVBG1F",
        // DB_PORT: 1433,
        // DB_USER: "sa",
        // DB_PASSWORD: "Supern0va",
        // DB_NAME: "radio_movil",

        // PORT: "3002",
        // JWT_SECRET: "radio_movil"
      },
      output: "./logs/out.log",   // console.log
      error: "./logs/error.log",  // console.error
      log: "./logs/combined.log"  // opcional
    }
  ]
};
