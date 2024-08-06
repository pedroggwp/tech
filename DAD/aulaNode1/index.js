console.log("Olá mundo")

// Common JS
// const matematica = require("./matematica")

// console.log(matematica.soma(5, 3));
// console.log(matematica.subtracao(5, 3));

// ESM
import { soma, subtracao } from "./matematica_es.js"

console.log(soma(3, 2))
console.log(subtracao(3, 2))
