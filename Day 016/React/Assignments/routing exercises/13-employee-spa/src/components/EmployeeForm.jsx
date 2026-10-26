import { useState } from 'react'
import { Link } from 'react-router-dom'
import { DEPARTMENTS } from '../context/EmployeeContext.jsx'
import { validateEmployee } from '../validateEmployee.js'

const EMPTY = { name: '', email: '', department: '', designation: '', salary: '' }

const FIELDS = [
  { name: 'name', label: 'Name' },
  { name: 'email', label: 'Email', type: 'email' },
  { name: 'department', label: 'Department', options: DEPARTMENTS },
  { name: 'designation', label: 'Designation' },
  { name: 'salary', label: 'Salary (₹)', type: 'number' },
]

function EmployeeForm({ initial = EMPTY, otherEmployees, submitLabel, cancelTo, onSubmit }) {
  const [form, setForm] = useState({ ...initial, salary: String(initial.salary ?? '') })
  const [touched, setTouched] = useState({})
  const errors = validateEmployee(form, otherEmployees)
  const errorFor = (name) => (touched[name] ? errors[name] : '')

  const handleChange = (event) => setForm({ ...form, [event.target.name]: event.target.value })
  const handleBlur = (event) => setTouched({ ...touched, [event.target.name]: true })

  const handleSubmit = (event) => {
    event.preventDefault()
    if (Object.keys(errors).length > 0) {
      setTouched(Object.fromEntries(FIELDS.map((f) => [f.name, true])))
      return
    }
    onSubmit({
      name: form.name.trim(),
      email: form.email.trim().toLowerCase(),
      department: form.department,
      designation: form.designation.trim(),
      salary: Number(form.salary),
    })
  }

  return (
    <form className="card form" onSubmit={handleSubmit} noValidate>
      {FIELDS.map((field) => {
        const error = errorFor(field.name)
        const common = {
          id: field.name,
          name: field.name,
          value: form[field.name],
          onChange: handleChange,
          onBlur: handleBlur,
          className: error ? 'input input--invalid' : 'input',
          'aria-invalid': Boolean(error),
          'aria-describedby': error ? `${field.name}-error` : undefined,
        }
        return (
          <div key={field.name} className="field">
            <label htmlFor={field.name}>{field.label}</label>
            {field.options ? (
              <select {...common}>
                <option value="">Select</option>
                {field.options.map((o) => <option key={o} value={o}>{o}</option>)}
              </select>
            ) : (
              <input type={field.type ?? 'text'} {...common} />
            )}
            {error && <p id={`${field.name}-error`} className="error">{error}</p>}
          </div>
        )
      })}
      <div className="form__actions">
        <button type="submit" className="button button--primary">{submitLabel}</button>
        <Link to={cancelTo} className="button">Cancel</Link>
      </div>
    </form>
  )
}

export default EmployeeForm
