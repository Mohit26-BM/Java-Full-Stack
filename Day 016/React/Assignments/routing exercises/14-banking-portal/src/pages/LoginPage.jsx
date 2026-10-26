import { useState } from 'react'
import { Navigate, useLocation, useNavigate } from 'react-router-dom'
import { useBank } from '../state/BankContext.jsx'

function LoginPage() {
  const { user, login } = useBank()
  const navigate = useNavigate()
  const location = useLocation()
  const from = location.state?.from ?? '/dashboard'
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  if (user) return <Navigate to={from} replace />

  const handleSubmit = (event) => {
    event.preventDefault()
    if (!username.trim() || !password) return setError('Enter your username and password.')
    if (!login(username.trim(), password)) return setError('Invalid username or password.')
    navigate(from, { replace: true })
  }

  return (
    <form className="card form narrow" onSubmit={handleSubmit} noValidate>
      <h1 className="page-title">Customer login</h1>
      {location.state?.from && <p className="notice" role="status">Please log in to continue to <code>{location.state.from}</code>.</p>}
      <label className="field">
        Username
        <input className="input" value={username} onChange={(e) => setUsername(e.target.value)} autoComplete="username" />
      </label>
      <label className="field">
        Password
        <input className="input" type="password" value={password} onChange={(e) => setPassword(e.target.value)}
          autoComplete="current-password" />
      </label>
      {error && <p className="error" role="alert">{error}</p>}
      <button type="submit" className="button button--primary">Log in</button>
      <p className="muted small">Demo customer: priya / bank@123</p>
    </form>
  )
}

export default LoginPage
