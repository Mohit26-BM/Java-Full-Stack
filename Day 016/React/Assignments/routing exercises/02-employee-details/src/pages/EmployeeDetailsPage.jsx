import { Link, useParams } from 'react-router-dom'
import EmployeeNotFound from '../components/EmployeeNotFound.jsx'
import { findEmployee } from '../employees.js'

function EmployeeDetailsPage({ employees }) {
  const { employeeId } = useParams()
  const employee = findEmployee(employees, employeeId)

  if (!employee) {
    return <EmployeeNotFound employeeId={employeeId} />
  }

  return (
    <section className="card details">
      <h1 className="page-title">{employee.name}</h1>
      <dl className="details__list">
        <dt>Employee ID</dt><dd>{employee.id}</dd>
        <dt>Email</dt><dd>{employee.email}</dd>
        <dt>Department</dt><dd>{employee.department}</dd>
        <dt>Role</dt><dd>{employee.role}</dd>
        <dt>Location</dt><dd>{employee.location}</dd>
      </dl>
      <div className="details__actions">
        <Link to="/employees" className="button">Back to list</Link>
        <Link to={`/employees/${employee.id}/edit`} className="button button--primary">Edit</Link>
      </div>
    </section>
  )
}

export default EmployeeDetailsPage
