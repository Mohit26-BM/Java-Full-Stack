import { Link } from 'react-router-dom'
import { departments } from '../departments.js'

function DepartmentListPage() {
  return (
    <section>
      <h1 className="page-title">Departments</h1>
      <ul className="department-list">
        {departments.map((department) => (
          <li key={department.id}>
            <Link to={`/departments/${department.id}/overview`} className="card department-list__link">
              <strong>{department.name}</strong>
              <span className="muted">{department.employees.length} employees · {department.location}</span>
            </Link>
          </li>
        ))}
      </ul>
    </section>
  )
}

export default DepartmentListPage
