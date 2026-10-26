import { Link } from 'react-router-dom'

function EmployeeListPage({ employees }) {
  return (
    <section>
      <h1 className="page-title">Employees</h1>
      <ul className="employee-list">
        {employees.map((employee) => (
          <li key={employee.id}>
            <Link to={`/employees/${employee.id}`} className="card employee-list__link">
              <strong>{employee.name}</strong>
              <span className="muted">{employee.role}</span>
            </Link>
          </li>
        ))}
      </ul>
    </section>
  )
}

export default EmployeeListPage
