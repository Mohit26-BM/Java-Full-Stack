function StudentList({ students, passMark, emptyText }) {
  if (students.length === 0) {
    return <p className="muted">{emptyText}</p>
  }
  return (
    <table className="data-table">
      <thead>
        <tr><th>Name</th><th>Marks</th><th>Result</th></tr>
      </thead>
      <tbody>
        {students.map((student) => {
          const passed = student.marks >= passMark
          return (
            <tr key={student.id}>
              <td>{student.name}</td>
              <td>{student.marks}</td>
              <td className={passed ? 'result result--pass' : 'result result--fail'}>{passed ? 'Passed' : 'Failed'}</td>
            </tr>
          )
        })}
      </tbody>
    </table>
  )
}

export default StudentList
