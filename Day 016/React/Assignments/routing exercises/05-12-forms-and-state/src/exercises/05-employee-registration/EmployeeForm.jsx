import { useState } from 'react'

export const NAME_LIMIT = 50
export const DEPARTMENTS = ['Engineering', 'Finance', 'Human Resources', 'Sales', 'Quality']
const EMPTY = { name: '', email: '', department: '', designation: '', salary: '' }

function EmployeeForm({ onSubmit }) {
  const [form, setForm] = useState(EMPTY)
  const [error, setError] = useState('')
  const remaining = NAME_LIMIT - form.name.length

  const handleChange = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value })
  }

  const handleSubmit = (event) => {
    event.preventDefault()
    const missing = Object.entries(form).filter(([, value]) => !value.trim()).map(([key]) => key)
    if (missing.length > 0) {
      setError(`Please fill in: ${missing.join(', ')}`)
      return
    }
    if (!(Number(form.salary) > 0)) {
      setError('Salary must be a positive number')
      return
    }
    onSubmit({ ...form, name: form.name.trim(), salary: Number(form.salary) })
    setForm(EMPTY)
    setError('')
  }

  return (
    <form className="card form" onSubmit={handleSubmit} noValidate>
      <div className="field">
        <label htmlFor="employee-name">Employee name</label>
        <input id="employee-name" className="input" name="name" value={form.name} onChange={handleChange}
          maxLength={NAME_LIMIT} aria-describedby="name-remaining" />
        <span id="name-remaining" className={remaining <= 10 ? 'hint hint--warning' : 'hint'}>
          {remaining} characters remaining
        </span>
      </div>
      <label className="field">
        Email
        <input className="input" type="email" name="email" value={form.email} onChange={handleChange} />
      </label>
      <label className="field">
        Department
        <select className="input" name="department" value={form.department} onChange={handleChange}>
          <option value="">Select a department</option>
          {DEPARTMENTS.map((d) => <option key={d} value={d}>{d}</option>)}
        </select>
      </label>
      <label className="field">
        Designation
        <input className="input" name="designation" value={form.designation} onChange={handleChange} />
      </label>
      <label className="field">
        Salary (₹)
        <input className="input" type="number" min="1" name="salary" value={form.salary} onChange={handleChange} />
      </label>
      {error && <p className="error" role="alert">{error}</p>}
      <div className="form__actions">
        <button type="submit" className="button button--primary">Register</button>
      </div>
    </form>
  )
}

export default EmployeeForm
