import { Link } from 'react-router-dom'
import { getUsers } from './users'

export default function Display() {
  const users = getUsers()

  return (
    <div className="card">
      <div className="card-header">
        <h1 className="text-2xl mb-0">Registered Users</h1>
      </div>
      <div className="card-body">
        {users.length === 0 ? (
          <p className="mb-0">
            No users yet. <Link to="/register">Register one</Link>.
          </p>
        ) : (
          <table className="table">
            <thead>
              <tr>
                <th>#</th>
                <th>Name</th>
                <th>Email</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user, index) => (
                <tr key={user.email}>
                  <td>{index + 1}</td>
                  <td>{user.name}</td>
                  <td>{user.email}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  )
}
