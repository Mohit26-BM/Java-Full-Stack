import { employees } from '../data.js'

function EmployeesPage() {
  return (
    <section>
      <h1 className="page-title">Employees</h1>
      <ul className="item-list">
        {employees.map((employee) => (
          <li key={employee.id} className="card">
            <strong>{employee.name}</strong>
            <span className="muted"> — {employee.role}, {employee.department}</span>
          </li>
        ))}
      </ul>
    </section>
  )
}

export default EmployeesPage
