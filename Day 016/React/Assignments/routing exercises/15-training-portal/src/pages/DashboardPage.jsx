import { Link } from 'react-router-dom'
import { useTraining } from '../state/TrainingContext.jsx'
import { enrolledCount } from '../state/trainingReducer.js'

function DashboardPage() {
  const { state, participantById, courseById, resetData } = useTraining()
  const { trainers, participants, courses, enrollments } = state
  const active = enrollments.filter((e) => e.status === 'Enrolled').length
  const completed = enrollments.filter((e) => e.status === 'Completed').length
  const fullCourses = courses.filter((c) => enrolledCount(enrollments, c.id) >= c.capacity)
  const recent = [...enrollments].sort((a, b) => b.date.localeCompare(a.date)).slice(0, 5)

  const stats = [
    ['Trainers', trainers.length, '/trainers'],
    ['Participants', participants.length, '/participants'],
    ['Courses', courses.length, '/courses'],
    ['Active enrollments', active, '/enrollments'],
    ['Completed', completed, '/enrollments'],
  ]

  return (
    <section>
      <div className="page-head">
        <h1 className="page-title">Dashboard</h1>
        <button type="button" className="button" onClick={resetData}>Reset to sample data</button>
      </div>
      <div className="stats">
        {stats.map(([label, value, to]) => (
          <Link key={label} to={to} className="card stat">
            <span className="muted">{label}</span>
            <strong className="stat__value" aria-label={label}>{value}</strong>
          </Link>
        ))}
      </div>
      <div className="two-col">
        <div className="card">
          <h2 className="section-title flush">Recent enrollments</h2>
          {recent.length === 0 ? <p className="muted">No enrollments yet.</p> : (
            <ul className="plain-list">
              {recent.map((e) => (
                <li key={e.id}>
                  <Link to={`/participants/${e.participantId}`}>{participantById(e.participantId)?.name}</Link>
                  {' → '}
                  <Link to={`/courses/${e.courseId}`}>{courseById(e.courseId)?.title}</Link>
                  <span className="muted"> · {e.status}</span>
                </li>
              ))}
            </ul>
          )}
        </div>
        <div className="card">
          <h2 className="section-title flush">Full courses</h2>
          {fullCourses.length === 0 ? <p className="muted">Every course still has seats.</p> : (
            <ul className="plain-list">
              {fullCourses.map((c) => <li key={c.id}><Link to={`/courses/${c.id}`}>{c.title}</Link> ({c.capacity} seats)</li>)}
            </ul>
          )}
        </div>
      </div>
    </section>
  )
}

export default DashboardPage
