import { Link } from 'react-router-dom'

function EmployeeNotFound({ employeeId }) {
  return (
    <section className="card not-found">
      <h1 className="page-title">Employee not found</h1>
      <p className="muted">There is no employee with the ID <code>{employeeId}</code>.</p>
      <Link to="/employees">Back to the employee list</Link>
    </section>
  )
}

export default EmployeeNotFound
