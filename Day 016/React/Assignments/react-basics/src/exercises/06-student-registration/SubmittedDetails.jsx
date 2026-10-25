function SubmittedDetails({ student }) {
  return (
    <div className="registration__success" role="status">
      <p className="registration__success-title">Registration successful!</p>
      <dl className="registration__details">
        <dt>Name</dt><dd>{student.name}</dd>
        <dt>Email</dt><dd>{student.email}</dd>
        <dt>Age</dt><dd>{student.age}</dd>
        <dt>Course</dt><dd>{student.course}</dd>
        <dt>Mobile</dt><dd>{student.mobile}</dd>
      </dl>
    </div>
  )
}

export default SubmittedDetails
