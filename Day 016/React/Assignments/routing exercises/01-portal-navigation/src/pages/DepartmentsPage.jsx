import { departments } from '../data.js'

function DepartmentsPage() {
  return (
    <section>
      <h1 className="page-title">Departments</h1>
      <ul className="item-list">
        {departments.map((department) => (
          <li key={department.name} className="card">
            <strong>{department.name}</strong>
            <span className="muted"> — head: {department.head}, {department.location}</span>
          </li>
        ))}
      </ul>
    </section>
  )
}

export default DepartmentsPage
