import { useState } from 'react'
import FormField from '../../shared/FormField.jsx'
import { MIN_PASSWORD, validateEmployee } from './validateEmployee.js'

const EMPTY = { name: '', email: '', mobile: '', password: '', confirmPassword: '' }
const FIELDS = [
  { name: 'name', label: 'Name', type: 'text' },
  { name: 'email', label: 'Email', type: 'email' },
  { name: 'mobile', label: 'Mobile number', type: 'tel' },
  { name: 'password', label: `Password (min ${MIN_PASSWORD} characters)`, type: 'password' },
  { name: 'confirmPassword', label: 'Confirm password', type: 'password' },
]

function ValidatedFormApp() {
  const [form, setForm] = useState(EMPTY)
  const [touched, setTouched] = useState({})
  const [registered, setRegistered] = useState(null)

  const errors = validateEmployee(form)
  const isValid = Object.keys(errors).length === 0
  const errorFor = (name) => (touched[name] ? errors[name] : '')

  const handleChange = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value })
    setRegistered(null)
  }

  const handleBlur = (event) => setTouched({ ...touched, [event.target.name]: true })

  const handleSubmit = (event) => {
    event.preventDefault()
    if (!isValid) {
      setTouched(Object.fromEntries(FIELDS.map((f) => [f.name, true])))
      return
    }
    setRegistered({ name: form.name.trim(), email: form.email.trim(), mobile: form.mobile })
    setForm(EMPTY)
    setTouched({})
  }

  return (
    <section>
      <h1 className="page-title">Employee Form with Validation</h1>
      <form className="card form" onSubmit={handleSubmit} noValidate>
        {FIELDS.map((field) => (
          <FormField key={field.name} id={field.name} label={field.label} error={errorFor(field.name)}>
            <input
              id={field.name}
              name={field.name}
              type={field.type}
              value={form[field.name]}
              onChange={handleChange}
              onBlur={handleBlur}
              className={errorFor(field.name) ? 'input input--invalid' : 'input'}
              aria-invalid={Boolean(errorFor(field.name))}
              aria-describedby={errorFor(field.name) ? `${field.name}-error` : undefined}
            />
          </FormField>
        ))}
        <div className="form__actions">
          <button type="submit" className="button button--primary" disabled={!isValid}>Submit</button>
        </div>
        {registered && (
          <p className="success" role="status">
            Registered {registered.name} ({registered.email}, {registered.mobile}).
          </p>
        )}
      </form>
    </section>
  )
}

export default ValidatedFormApp
