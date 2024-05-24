import { useNavigate } from 'react-router-dom';
import './App.css';
import React from 'react';

function App() {
  const [userName, setUserName] = React.useState('');
  const [password, setPassword] = React.useState('');
  const [message, setMessage] = React.useState('');
  const navigate = useNavigate(); 

  function handleSubmit(event) {
    event.preventDefault();
    const options = {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        userName: userName,
        password: password,
      })
    };
    fetch('/createUser', options)
      .then(res => res.json())
      .then(apiResponse => {
        if(apiResponse.status){
          setMessage("Your account has been created");
          setUserName('');
          setPassword('');
        }else{
          setMessage(apiResponse.message);
        }
      }); // after api call finishes
  }


  function handleLogin(event) {
    event.preventDefault();
    const options = {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        userName: userName,
        password: password,
      })
    };
    fetch('/login', options)
      .then(res => {
          console.log(res)
          if(res.status === 200){
            // login worked!
            navigate("/home");
          }else{
            setMessage('Incorrect credentials!');
          }
      }); // after api call finishes
  }

  return (
    <div className="App">
      <h1>Sign up or log in!</h1>
      <form>
        <div>{message}</div>
        <div>
          <label>User Name:</label>
          <input value={userName} onChange={(event) => setUserName(event.target.value)} />
        </div>
        <div>
          <label>Password:</label>
          <input value={password} onChange={(event) => setPassword(event.target.value)} />
        </div>
        <div>
          <button onClick={handleSubmit}>Create Account</button>
          <button onClick={handleLogin}>Log In</button>
        </div>
      </form>
    </div>
  );
}

export default App;
