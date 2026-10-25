import { Link, Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth.jsx'
import AuthShell from '../components/AuthShell.jsx'
import CustomerForm from '../components/CustomerForm.jsx'

export default function Register() {
  const { user, register } = useAuth()
  const navigate = useNavigate()

  if (user) return <Navigate to="/" replace />

  return (
    <AuthShell wide>
      <h1 className="h2 mb-1">Create your account</h1>
      <p className="text-secondary mb-4">You must be at least 18 years old to register.</p>
      <CustomerForm
        withPassword
        submitLabel="Register"
        onSubmit={async (payload) => {
          await register(payload)
          navigate('/', { replace: true })
        }}
      />
      <p className="text-center mt-4 mb-0 text-secondary">
        Already registered? <Link className="auth-link" to="/login">Sign in</Link>
      </p>
    </AuthShell>
  )
}
