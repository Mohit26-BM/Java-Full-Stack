import { useState } from 'react'
import { ErrorAlert } from './Feedback.jsx'
import PasswordInput from './PasswordInput.jsx'

const EMPTY = { name: '', email: '', mobile: '', dateOfBirth: '', gender: '', city: '', password: '' }

/**
 * Shared customer details form. With withPassword it registers a login too;
 * onSubmit receives the payload and may throw to show an error.
 */
export default function CustomerForm({ onSubmit, withPassword = false, submitLabel = 'Save', onCancel }) {
  const [form, setForm] = useState(EMPTY)
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  const update = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    setBusy(true)
    try {
      const { password, ...details } = form
      await onSubmit(withPassword ? { ...details, password } : details)
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  const field = (name, label, props = {}) => (
    <div className="col-md-6">
      <label htmlFor={`cf-${name}`} className="form-label">{label}</label>
      <input id={`cf-${name}`} name={name} className="form-control" value={form[name]} onChange={update}
        required {...props} />
    </div>
  )

  return (
    <form onSubmit={submit}>
      <ErrorAlert message={error} />
      <div className="row g-3">
        {field('name', 'Full name', { maxLength: 50 })}
        {field('email', 'Email', { type: 'email', maxLength: 100, autoComplete: 'email' })}
        {field('mobile', 'Mobile number', { inputMode: 'numeric', maxLength: 10, placeholder: '10 digits, starts 6-9' })}
        {field('dateOfBirth', 'Date of birth', { type: 'date', max: new Date().toISOString().slice(0, 10) })}
        <div className="col-md-6">
          <label htmlFor="cf-gender" className="form-label">Gender</label>
          <select id="cf-gender" name="gender" className="form-select" value={form.gender} onChange={update} required>
            <option value="">Select...</option>
            <option value="MALE">Male</option>
            <option value="FEMALE">Female</option>
            <option value="OTHERS">Others</option>
          </select>
        </div>
        {field('city', 'City', { maxLength: 60 })}
        {withPassword && (
          <div className="col-md-6">
            <label htmlFor="cf-password" className="form-label">Password</label>
            <PasswordInput id="cf-password" name="password" value={form.password} onChange={update} required
              minLength={8} maxLength={72} autoComplete="new-password" placeholder="8+ characters, letter and digit" />
          </div>
        )}
      </div>
      <div className="d-flex gap-2 mt-4">
        <button className="btn btn-primary" disabled={busy}>{busy ? 'Please wait...' : submitLabel}</button>
        {onCancel && <button type="button" className="btn btn-outline-secondary" onClick={onCancel}>Cancel</button>}
      </div>
    </form>
  )
}
