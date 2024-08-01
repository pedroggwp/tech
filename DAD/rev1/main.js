const btn = document.getElementsByTagName("button")[0];
btn.addEventListener('click', () => {
    window.confirm("clicou");
})

const btn2 = document.getElementsByTagName("button")[1];
btn2.addEventListener('click', () => {
    window.confirm("?");
})

const retangle = document.getElementsByClassName("ret")[0]
retangle.addEventListener("click", () => {
    window.confirm("-----------")
})