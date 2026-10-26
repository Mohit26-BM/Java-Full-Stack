import { useOutletContext } from 'react-router-dom'

function EmployeesTab() {
  const { employees } = useOutletContext()
  return (
    <div>
      <h2 className="tab-title">Employees</h2>
      <ul className="tab-list">
        {employees.map((employee) => (
          <li key={employee.id}>{employee.name} <span className="muted">— {employee.role}</span></li>
        ))}
      </ul>
    </div>
  )
}

export default EmployeesTab
