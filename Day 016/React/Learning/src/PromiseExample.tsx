import { useState, type FormEvent } from "react";
type LoginResult = { message: string };

function fakeLogin(username: string, password: string): Promise<LoginResult> {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (username === "student" && password === "react123") {
        resolve({ message: "Login successful." });
      } else {
        reject(new Error("Invalid username or password."));
      }
    }, 1200);
  });
}



function PromiseExample() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setMessage("");
    setLoading(true);

    try {
      const result = await fakeLogin(username, password);
      setMessage(result.message);
    } catch (error) {
      setMessage(
        error instanceof Error ? error.message : "Login failed."
      );
    }  finally {
      setLoading(false);
    }
  }

  return (
    <main>
      <h1>Async Login Demo</h1>
      <form onSubmit={handleSubmit}>
        <p>
          <label htmlFor="username">Username: </label>
          <input
            id="username"
            value={username}
            onChange={(event) => setUsername(event.target.value)}
            required
          />
        </p>
        <p>
          <label htmlFor="password">Password: </label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            required
          />
        </p>
        <button type="submit" disabled={loading}>
          {loading ? "Signing in..." : "Login"}
        </button>
      </form>
      {message && <p role="status">{message}</p>}
      <p>Demo credentials: student / react123</p>
    </main>
  );
}



export default PromiseExample;
