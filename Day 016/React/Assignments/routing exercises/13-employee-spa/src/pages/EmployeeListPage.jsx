import { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { useEmployees } from '../context/EmployeeContext.jsx'

function EmployeeListPage() {
  const { employees } = useEmployees()
  const { state } = useLocation()
  const [search, setSearch] = useState('')

  const term = search.trim().toLowerCase()
  const visible = employees.filter((e) =>
    e.name.toLowerCase().includes(term) || e.department.toLowerCase().includes(term))

  return (
    <section>
      <div className="page-head">
        <h1 className="page-title">Employees ({employees.length})</h1>
        <Link to="/employees/new" className="button button--primary">+ Add employee</Link>
      </div>
      {state?.message && <p className="success" role="status">{state.message}</p>}
      <input className="input search" type="search" placeholder="Search by name or department"
        aria-label="Search employees" value={search} onChange={(e) => setSearch(e.target.value)} />
      {employees.length === 0 ? (
        <p className="muted">No employees yet. <Link to="/employees/new">Add the first one.</Link></p>
      ) : visible.length === 0 ? (
        <p className="muted">No employees match "{search}".</p>
      ) : (
        <table className="data-table">
          <thead>
            <tr><th>Name</th><th>Department</th><th>Designation</th><th /></tr>
          </thead>
          <tbody>
            {visible.map((employee) => (
              <tr key={employee.id}>
                <td><Link to={`/employees/${employee.id}`}>{employee.name}</Link></td>
                <td>{employee.department}</td>
                <td>{employee.designation}</td>
                <td className="table-actions">
                  <Link to={`/employees/${employee.id}/edit`} aria-label={`Edit ${employee.name}`}>Edit</Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  )
}

export default EmployeeListPage
