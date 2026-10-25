function EmployeeTable({ employees }) {
  return (
    <table className="employee-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Name</th>
          <th>Department</th>
          <th>Role</th>
        </tr>
      </thead>
      <tbody>
        {employees.map((employee) => (
          <tr key={employee.id}>
            <td>{employee.id}</td>
            <td>{employee.name}</td>
            <td>{employee.department}</td>
            <td>{employee.role}</td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}

export default EmployeeTable
