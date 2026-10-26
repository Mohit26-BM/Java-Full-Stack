import { useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import EmployeeNotFound from '../components/EmployeeNotFound.jsx'
import { findEmployee } from '../employees.js'

const FIELDS = [
  ['name', 'Name'],
  ['email', 'Email'],
  ['department', 'Department'],
  ['role', 'Role'],
  ['location', 'Location'],
]

function EmployeeEditForm({ employee, onSave }) {
  const navigate = useNavigate()
  const [form, setForm] = useState(employee)
  const blank = FIELDS.some(([name]) => !form[name].trim())

  const handleSubmit = (event) => {
    event.preventDefault()
    if (blank) return
    onSave(form)
    navigate(`/employees/${employee.id}`)
  }

  return (
    <form className="card edit-form" onSubmit={handleSubmit}>
      <h1 className="page-title">Edit {employee.name}</h1>
      {FIELDS.map(([name, label]) => (
        <label key={name} className="field">
          {label}
          <input
            className="input"
            name={name}
            value={form[name]}
            onChange={(event) => setForm({ ...form, [name]: event.target.value })}
          />
        </label>
      ))}
      {blank && <p className="error">All fields are required.</p>}
      <div className="details__actions">
        <Link to={`/employees/${employee.id}`} className="button">Cancel</Link>
        <button type="submit" className="button button--primary" disabled={blank}>Save</button>
      </div>
    </form>
  )
}

function EmployeeEditPage({ employees, onSave }) {
  const { employeeId } = useParams()
  const employee = findEmployee(employees, employeeId)
  return employee
    ? <EmployeeEditForm key={employee.id} employee={employee} onSave={onSave} />
    : <EmployeeNotFound employeeId={employeeId} />
}

export default EmployeeEditPage
