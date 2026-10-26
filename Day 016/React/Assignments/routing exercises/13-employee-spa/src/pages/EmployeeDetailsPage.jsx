import { useState } from 'react'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'
import EmployeeNotFound from '../components/EmployeeNotFound.jsx'
import { useEmployees } from '../context/EmployeeContext.jsx'

function EmployeeDetailsPage() {
  const { id } = useParams()
  const { state } = useLocation()
  const navigate = useNavigate()
  const { findEmployee, deleteEmployee } = useEmployees()
  const [confirming, setConfirming] = useState(false)
  const employee = findEmployee(id)

  if (!employee) return <EmployeeNotFound id={id} />

  const handleDelete = () => {
    deleteEmployee(employee.id)
    navigate('/employees', { replace: true, state: { message: `${employee.name} was deleted.` } })
  }

  return (
    <section className="card narrow">
      {state?.message && <p className="success" role="status">{state.message}</p>}
      <h1 className="page-title">{employee.name}</h1>
      <dl className="details">
        <dt>Employee ID</dt><dd>{employee.id}</dd>
        <dt>Email</dt><dd>{employee.email}</dd>
        <dt>Department</dt><dd>{employee.department}</dd>
        <dt>Designation</dt><dd>{employee.designation}</dd>
        <dt>Salary</dt><dd>₹{employee.salary.toLocaleString('en-IN')}</dd>
      </dl>
      {confirming ? (
        <div className="confirm" role="alertdialog" aria-label="Confirm delete">
          <p>Delete {employee.name}? This cannot be undone.</p>
          <div className="form__actions">
            <button type="button" className="button button--danger-solid" onClick={handleDelete}>Yes, delete</button>
            <button type="button" className="button" onClick={() => setConfirming(false)}>Cancel</button>
          </div>
        </div>
      ) : (
        <div className="form__actions">
          <Link to="/employees" className="button">Back to list</Link>
          <Link to={`/employees/${employee.id}/edit`} className="button button--primary">Edit</Link>
          <button type="button" className="button button--danger" onClick={() => setConfirming(true)}>Delete</button>
        </div>
      )}
    </section>
  )
}

export default EmployeeDetailsPage
