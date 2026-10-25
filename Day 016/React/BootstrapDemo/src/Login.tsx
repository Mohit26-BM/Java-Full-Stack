import { useState, type FormEvent } from 'react'
import { Link } from 'react-router-dom'
import { getUsers, type User } from './users'

export default function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loggedIn, setLoggedIn] = useState<User | null>(null)

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    const user = getUsers().find(
      u => u.email === email.trim() && u.password === password
    )
    if (user) {
      setError('')
      setLoggedIn(user)
    } else {
      setLoggedIn(null)
      setError('Invalid email or password.')
    }
  }

  return (
    <div className="card">
      <div className="card-header">
        <h1 className="text-2xl mb-0">Login</h1>
      </div>
      <div className="card-body">
        <form onSubmit={handleSubmit} noValidate>
          <div className="mb-3">
            <label htmlFor="email" className="form-label">Email</label>
            <input
              id="email"
              type="email"
              className="form-control"
              value={email}
              onChange={event => setEmail(event.target.value)}
            />
          </div>
          <div className="mb-3">
            <label htmlFor="password" className="form-label">Password</label>
            <input
              id="password"
              type="password"
              className="form-control"
              value={password}
              onChange={event => setPassword(event.target.value)}
            />
          </div>

          {error && <div className="alert theme-danger" role="alert">{error}</div>}
          {loggedIn && (
            <div className="alert theme-success" role="status">
              Welcome, {loggedIn.name}!
            </div>
          )}

          <button type="submit" className="btn btn-solid theme-primary">Login</button>
        </form>
        <p className="mt-3 mb-0">
          No account? <Link to="/register">Register</Link>
        </p>
      </div>
    </div>
  )
}
