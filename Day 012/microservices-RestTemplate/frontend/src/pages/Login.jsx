import { useState } from 'react'
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth.jsx'
import AuthShell from '../components/AuthShell.jsx'
import { ErrorAlert } from '../components/Feedback.jsx'
import { MailIcon } from '../components/Icons.jsx'
import PasswordInput from '../components/PasswordInput.jsx'

export default function Login() {
  const { user, login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  if (user) return <Navigate to="/" replace />

  const update = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    setBusy(true)
    try {
      await login(form)
      navigate(location.state?.from?.pathname ?? '/', { replace: true })
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <AuthShell>
      <h1 className="h2 mb-1">Welcome back</h1>
      <p className="text-secondary mb-4">Sign in to manage your accounts and loans.</p>
      <ErrorAlert message={error} />
      <form onSubmit={submit} noValidate>
        <div className="mb-3">
          <label htmlFor="email" className="form-label">Email</label>
          <div className="input-icon">
            <MailIcon className="lead-icon" />
            <input id="email" name="email" type="email" className="form-control" autoComplete="username"
              placeholder="you@example.com" value={form.email} onChange={update} required autoFocus />
          </div>
        </div>
        <div className="mb-4">
          <label htmlFor="password" className="form-label">Password</label>
          <PasswordInput id="password" name="password" autoComplete="current-password"
            placeholder="Your password" value={form.password} onChange={update} required />
        </div>
        <button className="btn btn-primary w-100 py-2" disabled={busy || !form.email || !form.password}>
          {busy && <span className="spinner-border spinner-border-sm" aria-hidden="true" />}
          {busy ? 'Signing in...' : 'Sign in'}
        </button>
      </form>
      <p className="text-center mt-4 mb-0 text-secondary">
        New customer? <Link className="auth-link" to="/register">Create an account</Link>
      </p>
    </AuthShell>
  )
}
