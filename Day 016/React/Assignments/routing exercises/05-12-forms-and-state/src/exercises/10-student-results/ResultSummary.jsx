function ResultSummary({ students, passMark }) {
  const passed = students.filter((s) => s.marks >= passMark).length
  const average = students.length ? students.reduce((sum, s) => sum + s.marks, 0) / students.length : 0

  return (
    <dl className="summary">
      <div className="summary__item"><dt>Students</dt><dd aria-label="Students">{students.length}</dd></div>
      <div className="summary__item"><dt>Average marks</dt><dd aria-label="Average marks">{average.toFixed(1)}</dd></div>
      <div className="summary__item"><dt>Passed (≥ {passMark})</dt><dd aria-label="Passed">{passed}</dd></div>
    </dl>
  )
}

export default ResultSummary
