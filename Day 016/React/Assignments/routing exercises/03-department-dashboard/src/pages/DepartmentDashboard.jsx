import { Link, NavLink, Outlet, useParams } from 'react-router-dom'
import { findDepartment } from '../departments.js'
import './DepartmentDashboard.css'

const SECTIONS = [
  ['overview', 'Overview'],
  ['employees', 'Employees'],
  ['projects', 'Projects'],
  ['reports', 'Reports'],
]

function DepartmentDashboard() {
  const { departmentId } = useParams()
  const department = findDepartment(departmentId)

  if (!department) {
    return (
      <section className="card">
        <h1 className="page-title">Department not found</h1>
        <p className="muted">There is no department <code>{departmentId}</code>.</p>
        <Link to="/departments">Back to departments</Link>
      </section>
    )
  }

  return (
    <section className="dashboard">
      <div className="dashboard__header">
        <h1 className="page-title">{department.name}</h1>
        <p className="muted">Head: {department.head} · {department.location}</p>
      </div>
      <nav className="dashboard__tabs" aria-label="Dashboard sections">
        {SECTIONS.map(([path, label]) => (
          <NavLink
            key={path}
            to={path}
            className={({ isActive }) => (isActive ? 'dashboard__tab dashboard__tab--active' : 'dashboard__tab')}
          >
            {label}
          </NavLink>
        ))}
      </nav>
      <div className="dashboard__content">
        <Outlet context={department} />
      </div>
    </section>
  )
}

export default DepartmentDashboard
