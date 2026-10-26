function SubmittedEmployees({ employees }) {
  return (
    <>
      <h2 className="section-title">Submitted employees ({employees.length})</h2>
      {employees.length === 0 ? (
        <p className="muted">Nothing submitted yet.</p>
      ) : (
        <table className="data-table">
          <thead>
            <tr><th>Name</th><th>Email</th><th>Department</th><th>Designation</th><th>Salary</th></tr>
          </thead>
          <tbody>
            {employees.map((e) => (
              <tr key={e.id}>
                <td>{e.name}</td>
                <td>{e.email}</td>
                <td>{e.department}</td>
                <td>{e.designation}</td>
                <td>₹{e.salary.toLocaleString('en-IN')}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </>
  )
}

export default SubmittedEmployees
