import { useState } from "react";

type User = {
  id: number;
  name: string;
  email: string;
};

function PromiseRest() {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function loadUsers() {
    setLoading(true);
    setError("");

    try {
      const response = await fetch(
        "https://jsonplaceholder.typicode.com/users"
      );

      if (!response.ok) {
        throw new Error(`Request failed: ${response.status}`);
      }

      const data: User[] = await response.json();
      setUsers(data);
    } catch (error) {
      setError(
        error instanceof Error ? error.message : "An unknown error occurred."
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <main>
      <h1>Users from REST API</h1>
      <button onClick={loadUsers} disabled={loading}>
        {loading ? "Loading users..." : "Load Users"}
      </button>

      {loading && <p>Loading data from the server...</p>}
      {error && <p role="alert">Error: {error}</p>}

      {!loading && !error && users.length === 0 && (
        <p>No users loaded. Click Load Users.</p>
      )}

      <table cellPadding="8">
      <thead>
        <tr>
          <th>Name</th>
          <th>Email</th>
        </tr>
      </thead>
      <tbody>
        {users.length > 0 ? (
          users.map((user, index) => (
            <tr key={index}>
              <td>{user.name}</td>
              <td>{user.email}</td>
            </tr>
          ))
        ) : (
          <tr>
            <td>No users available</td>
          </tr>
        )}
      </tbody>
    </table>

    </main>
  );
}

export default PromiseRest;
