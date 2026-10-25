import './EmployeeCard.css'

function EmployeeCard({ employee }) {
  return (
    <article className="employee-card">
      <h2 className="employee-card__name">{employee.name}</h2>
      <p className="employee-card__id">ID: {employee.id}</p>
      <p className="employee-card__role">{employee.role} - {employee.department}</p>
      <p className="employee-card__location">Location: {employee.location}</p>
      <p className="employee-card__summary">{employee.summary}</p>
    </article>
  )
}

export default EmployeeCard
