import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { addUser, getUsers } from './users'

export default function Register() {
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const navigate = useNavigate()

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (!name.trim() || !email.trim() || !password) {
      setError('Please fill in all fields.')
      return
    }
    if (getUsers().some(user => user.email === email.trim())) {
      setError('This email is already registered.')
      return
    }

    addUser({ name: name.trim(), email: email.trim(), password })
    navigate('/display')
  }

  return (
    <div className="card">
      <div className="card-header">
        <h1 className="text-2xl mb-0">Register</h1>
      </div>
      <div className="card-body">
        <form onSubmit={handleSubmit} noValidate>
          <div className="mb-3">
            <label htmlFor="name" className="form-label">Name</label>
            <input
              id="name"
              className="form-control"
              value={name}
              onChange={event => setName(event.target.value)}
            />
          </div>
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

          <button type="submit" className="btn btn-solid theme-primary">Register</button>
        </form>
      </div>
    </div>
  )
}
