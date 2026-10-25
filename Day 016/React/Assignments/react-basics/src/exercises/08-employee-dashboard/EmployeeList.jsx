import { formatRupees } from './dashboardData.js'

function EmployeeList({ employees }) {
  if (employees.length === 0) {
    return <p className="dashboard__empty">No employees match these filters.</p>
  }
  return (
    <table className="dashboard__table">
      <thead>
        <tr>
          <th>Name</th>
          <th>Department</th>
          <th>Role</th>
          <th className="dashboard__number">Salary</th>
        </tr>
      </thead>
      <tbody>
        {employees.map((employee) => (
          <tr key={employee.id}>
            <td>{employee.name}</td>
            <td>{employee.department}</td>
            <td>{employee.role}</td>
            <td className="dashboard__number">{formatRupees(employee.salary)}</td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}

export default EmployeeList
