import { useState } from 'react'
import { Link } from 'react-router-dom'
import EnrollForm from '../components/EnrollForm.jsx'
import { useTraining } from '../state/TrainingContext.jsx'

const STATUSES = ['All', 'Enrolled', 'Completed']

function EnrollmentsPage() {
  const { state, participantById, courseById, completeEnrollment, cancelEnrollment } = useTraining()
  const [status, setStatus] = useState('All')
  const [search, setSearch] = useState('')

  const term = search.trim().toLowerCase()
  const visible = state.enrollments.filter((e) => {
    const participant = participantById(e.participantId)?.name ?? ''
    const course = courseById(e.courseId)?.title ?? ''
    return (status === 'All' || e.status === status)
      && (participant.toLowerCase().includes(term) || course.toLowerCase().includes(term))
  })

  return (
    <section>
      <h1 className="page-title">Enrollments ({state.enrollments.length})</h1>
      <EnrollForm />
      <div className="filters">
        <input type="search" className="input" placeholder="Search participant or course" aria-label="Search enrollments"
          value={search} onChange={(e) => setSearch(e.target.value)} />
        <select className="input" aria-label="Filter by status" value={status} onChange={(e) => setStatus(e.target.value)}>
          {STATUSES.map((s) => <option key={s} value={s}>{s}</option>)}
        </select>
      </div>
      {visible.length === 0 ? <p className="muted">No enrollments match these filters.</p> : (
        <table className="data-table">
          <thead><tr><th>ID</th><th>Participant</th><th>Course</th><th>Date</th><th>Status</th><th /></tr></thead>
          <tbody>
            {visible.map((e) => (
              <tr key={e.id}>
                <td>{e.id}</td>
                <td><Link to={`/participants/${e.participantId}`}>{participantById(e.participantId)?.name}</Link></td>
                <td><Link to={`/courses/${e.courseId}`}>{courseById(e.courseId)?.title}</Link></td>
                <td>{e.date}</td>
                <td>{e.status}</td>
                <td className="row-actions">
                  {e.status === 'Enrolled' && (
                    <>
                      <button type="button" className="button small-button" onClick={() => completeEnrollment(e.id)}
                        aria-label={`Mark ${e.id} completed`}>Complete</button>
                      <button type="button" className="button button--danger small-button" onClick={() => cancelEnrollment(e.id)}
                        aria-label={`Cancel ${e.id}`}>Cancel</button>
                    </>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  )
}

export default EnrollmentsPage
