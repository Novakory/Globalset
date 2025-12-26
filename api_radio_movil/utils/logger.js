import fs from "fs";
import path from "path";

const logPath = path.join(process.cwd(), "logs", "app.log");

// Asegurar que la carpeta exista
fs.mkdirSync(path.dirname(logPath), { recursive: true });

function writeLog(level, message) {
  const timestamp = new Date().toISOString();
  fs.appendFileSync(
    logPath,
    `[${timestamp}] [${level}] ${message}\r\n`
  );
}

// Guardar referencias originales
const originalLog = console.log;
const originalError = console.error;
const originalWarn = console.warn;

console.log = (...args) => {
  writeLog("INFO", args.join(" "));
  originalLog(...args);
};

console.error = (...args) => {
  writeLog("ERROR", args.join(" "));
  originalError(...args);
};

console.warn = (...args) => {
  writeLog("WARN", args.join(" "));
  originalWarn(...args);
};
