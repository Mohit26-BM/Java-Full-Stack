import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";

function Login(){

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const navigate = useNavigate();

  function handleLogin(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    // Validate empty fields
    if (!username.trim() || !password) {
      setError("Please enter both username and password.");
      return;
    }

    // Demo credentials
    if (username.trim() === "admin" && password === "admin123") {
      setError("");
      navigate("/success");
    } else {
      navigate("/failure");
    }
  }



    return(
       <div className="container">
      <form className="login-form" onSubmit={handleLogin}>
        <h2>User Login</h2>

        <label htmlFor="username">Username</label>
        <input
          id="username"
          type="text"
          placeholder="Enter username"
          value={username}
          onChange={(event) => {
            setUsername(event.target.value);
            setError("");
          }}
        />

        <label htmlFor="password">Password</label>
        <input
          id="password"
          type="password"
          placeholder="Enter password"
          value={password}
          onChange={(event) => {
            setPassword(event.target.value);
            setError("");
          }}
        />

        {error && <p className="error">{error}</p>}

        <button type="submit">Login</button>

        <p className="hint">
          Demo username: admin
          <br />
          Demo password: admin123
        </p>
      </form>
    </div>

    );
}
export default Login;
