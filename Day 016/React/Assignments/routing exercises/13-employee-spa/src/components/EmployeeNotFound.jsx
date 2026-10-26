import { Link } from 'react-router-dom'

function EmployeeNotFound({ id }) {
  return (
    <section className="card narrow">
      <h1 className="page-title">Employee not found</h1>
      <p className="muted">There is no employee with the ID <code>{id}</code>. It may have been deleted.</p>
      <Link to="/employees">Back to the employee list</Link>
    </section>
  )
}

export default EmployeeNotFound
