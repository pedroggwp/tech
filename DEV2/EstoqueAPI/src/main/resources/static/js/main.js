document.getElementById("list").addEventListener("click", (e) => {
    fetch(e.target.value)
        .then(data => data.json())
        .then(data => showData(data));
});

function showData(data) {
    const htmlTables = document.getElementsByTagName('table')
    const table = htmlTables.length > 0 ? htmlTables[0] : document.createElement('table')
    const tr = document.createElement('tr')
    for(const key in data[0]) {
        const th = document.createElement('th')
        th.innerText = key;
        tr.appendChild(th)
    }
    table.appendChild(tr)
    data.map(product => {
        const tr = document.createElement('tr')
        for(const value of Object.values(product)) {
            const td = document.createElement('td')
            td.innerText = value
            tr.appendChild(td)
        }
        table.appendChild(tr)
    })
    document.getElementsByTagName('body')[0].appendChild(table)
}

