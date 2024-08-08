import './App.css';

const name = "Pedro";
const students = ["Pablo", "Vanessa", "Robson"];

function App() {
  return (
    <div className="App">
      <h1>Olá {name}</h1>
      <h2>Alunos:</h2>
      <ul>
        {students.map(student => (
          <li key={student}>{student}</li>
        ))}
      </ul>
    </div>
  );
}

export default App;
