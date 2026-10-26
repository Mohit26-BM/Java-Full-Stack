import { Link, useLocation, useParams } from 'react-router-dom'
import EnrollForm from '../components/EnrollForm.jsx'
import NotFoundCard from '../components/NotFoundCard.jsx'
import { useTraining } from '../state/TrainingContext.jsx'
import { enrolledCount } from '../state/trainingReducer.js'

function CourseDetailsPage() {
  const { courseId } = useParams()
  const { state: navState } = useLocation()
  const { state, courseById, trainerById, participantById } = useTraining()
  const course = courseById(courseId)

  if (!course) return <NotFoundCard what="Course" id={courseId} backTo="/courses" backLabel="Back to courses" />

  const enrollments = state.enrollments.filter((e) => e.courseId === course.id)
  const taken = enrolledCount(state.enrollments, course.id)
  const trainer = trainerById(course.trainerId)

  return (
    <section>
      {navState?.message && <p className="success" role="status">{navState.message}</p>}
      <h1 className="page-title">{course.title}</h1>
      <div className="two-col">
        <div className="card">
          <dl className="details">
            <dt>ID</dt><dd>{course.id}</dd>
            <dt>Category</dt><dd>{course.category}</dd>
            <dt>Trainer</dt><dd>{trainer?.name}</dd>
            <dt>Starts</dt><dd>{course.startDate}</dd>
            <dt>Duration</dt><dd>{course.durationDays} days</dd>
            <dt>Seats</dt><dd aria-label="Seats">{taken} / {course.capacity}{taken >= course.capacity ? ' (full)' : ''}</dd>
          </dl>
        </div>
        <div>
          <h2 className="section-title flush">Enroll a participant</h2>
          <EnrollForm courseId={course.id} />
        </div>
      </div>
      <h2 className="section-title">Participants ({enrollments.length})</h2>
      {enrollments.length === 0 ? <p className="muted">No participants yet.</p> : (
        <ul className="plain-list">
          {enrollments.map((e) => (
            <li key={e.id}>
              <Link to={`/participants/${e.participantId}`}>{participantById(e.participantId)?.name}</Link>
              <span className="muted"> · {e.status}</span>
            </li>
          ))}
        </ul>
      )}
    </section>
  )
}

export default CourseDetailsPage
