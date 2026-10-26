import { useState } from 'react'
import { Navigate, useLocation, useNavigate } from 'react-router-dom'

function LoginPage({ loggedIn, onLogin }) {
  const navigate = useNavigate()
  const location = useLocation()
  const from = location.state?.from ?? '/admin'
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  if (loggedIn) {
    return <Navigate to={from} replace />
  }

  const handleSubmit = (event) => {
    event.preventDefault()
    if (!username.trim() || !password) {
      setError('Enter your username and password.')
    } else if (!onLogin(username.trim(), password)) {
      setError('Invalid username or password.')
    } else {
      navigate(from, { replace: true })
    }
  }

  return (
    <form className="card login" onSubmit={handleSubmit} noValidate>
      <h1 className="page-title">Login</h1>
      {location.state?.from && (
        <p className="login__notice" role="status">
          Please log in to open <code>{location.state.from}</code>.
        </p>
      )}
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
      <p className="muted login__hint">Demo account: admin / admin123</p>
    </form>
  )
}

export default LoginPage
