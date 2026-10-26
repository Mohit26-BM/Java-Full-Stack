import { Link, useLocation, useParams } from 'react-router-dom'
import EnrollForm from '../components/EnrollForm.jsx'
import NotFoundCard from '../components/NotFoundCard.jsx'
import { useTraining } from '../state/TrainingContext.jsx'

function ParticipantDetailsPage() {
  const { participantId } = useParams()
  const { state: navState } = useLocation()
  const { state, participantById, courseById } = useTraining()
  const participant = participantById(participantId)

  if (!participant) {
    return <NotFoundCard what="Participant" id={participantId} backTo="/participants" backLabel="Back to participants" />
  }
  const enrollments = state.enrollments.filter((e) => e.participantId === participant.id)

  return (
    <section>
      {navState?.message && <p className="success" role="status">{navState.message}</p>}
      <h1 className="page-title">{participant.name}</h1>
      <div className="two-col">
        <div className="card">
          <dl className="details">
            <dt>ID</dt><dd>{participant.id}</dd>
            <dt>Email</dt><dd>{participant.email}</dd>
            <dt>Mobile</dt><dd>{participant.phone}</dd>
            <dt>Organization</dt><dd>{participant.organization}</dd>
            <dt>Level</dt><dd>{participant.level}</dd>
          </dl>
        </div>
        <div>
          <h2 className="section-title flush">Enroll in a course</h2>
          <EnrollForm participantId={participant.id} />
        </div>
      </div>
      <h2 className="section-title">Courses ({enrollments.length})</h2>
      {enrollments.length === 0 ? <p className="muted">Not enrolled in any course yet.</p> : (
        <table className="data-table">
          <thead><tr><th>Course</th><th>Starts</th><th>Status</th></tr></thead>
          <tbody>
            {enrollments.map((e) => {
              const course = courseById(e.courseId)
              return (
                <tr key={e.id}>
                  <td><Link to={`/courses/${course.id}`}>{course.title}</Link></td>
                  <td>{course.startDate}</td>
                  <td>{e.status}</td>
                </tr>
              )
            })}
          </tbody>
        </table>
      )}
    </section>
  )
}

export default ParticipantDetailsPage
