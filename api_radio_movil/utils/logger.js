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
  writeLog("INFO", formatArgs(args));
  originalLog(...args);
};

console.error = (...args) => {
  writeLog("ERROR", formatArgs(args));
  originalError(...args);
};

console.warn = (...args) => {
  writeLog("WARN", formatArgs(args));
  originalWarn(...args);
};


function formatArgs(args) {
  return args.map(arg => {
    if (arg instanceof Error) {
      return `${arg.message}\n${arg.stack}`;
    }
    if (typeof arg === "object") {
      return safeStringify(arg);
    }
    return arg;
  }).join(" ");
}
