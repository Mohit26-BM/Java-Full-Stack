import { Link } from 'react-router-dom'
import { useTraining } from '../state/TrainingContext.jsx'
import { enrolledCount } from '../state/trainingReducer.js'

const percent = (part, whole) => (whole ? `${Math.round((part / whole) * 100)}%` : '—')

function ReportsPage() {
  const { state } = useTraining()
  const { courses, trainers, enrollments, participants } = state

  return (
    <section>
      <h1 className="page-title">Reports</h1>

      <h2 className="section-title">Course occupancy and completion</h2>
      <table className="data-table" aria-label="Course report">
        <thead><tr><th>Course</th><th>Enrolled</th><th>Capacity</th><th>Filled</th><th>Completed</th><th>Completion rate</th></tr></thead>
        <tbody>
          {courses.map((c) => {
            const taken = enrolledCount(enrollments, c.id)
            const completed = enrollments.filter((e) => e.courseId === c.id && e.status === 'Completed').length
            return (
              <tr key={c.id}>
                <td><Link to={`/courses/${c.id}`}>{c.title}</Link></td>
                <td>{taken}</td>
                <td>{c.capacity}</td>
                <td>{percent(taken, c.capacity)}</td>
                <td>{completed}</td>
                <td>{percent(completed, taken)}</td>
              </tr>
            )
          })}
        </tbody>
      </table>

      <h2 className="section-title">Trainer workload</h2>
      <table className="data-table" aria-label="Trainer report">
        <thead><tr><th>Trainer</th><th>Courses</th><th>Participants</th></tr></thead>
        <tbody>
          {trainers.map((t) => {
            const ids = courses.filter((c) => c.trainerId === t.id).map((c) => c.id)
            return (
              <tr key={t.id}>
                <td>{t.name}</td>
                <td>{ids.length}</td>
                <td>{enrollments.filter((e) => ids.includes(e.courseId)).length}</td>
              </tr>
            )
          })}
        </tbody>
      </table>

      <h2 className="section-title">Participants without a course</h2>
      {(() => {
        const idle = participants.filter((p) => !enrollments.some((e) => e.participantId === p.id))
        return idle.length === 0 ? <p className="muted">Every participant is enrolled in at least one course.</p> : (
          <ul className="plain-list">
            {idle.map((p) => <li key={p.id}><Link to={`/participants/${p.id}`}>{p.name}</Link></li>)}
          </ul>
        )
      })()}
    </section>
  )
}

export default ReportsPage
