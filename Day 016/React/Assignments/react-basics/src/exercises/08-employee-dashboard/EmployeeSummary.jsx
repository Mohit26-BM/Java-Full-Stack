function EmployeeSummary({ department, shown, total }) {
  const where = department === 'All' ? 'all departments' : department
  return (
    <p className="dashboard__summary">
      Showing {shown} of {total} employees in {where}.
    </p>
  )
}

export default EmployeeSummary
