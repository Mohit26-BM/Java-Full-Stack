import { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { EXPERIENCE_LEVELS } from '../state/sampleData.js'
import { useTraining } from '../state/TrainingContext.jsx'

function ParticipantsPage() {
  const { state } = useTraining()
  const location = useLocation()
  const [search, setSearch] = useState('')
  const [level, setLevel] = useState('')

  const term = search.trim().toLowerCase()
  const visible = state.participants.filter((p) =>
    (p.name.toLowerCase().includes(term) || p.organization.toLowerCase().includes(term) || p.id.toLowerCase().includes(term))
    && (!level || p.level === level))

  return (
    <section>
      <div className="page-head">
        <h1 className="page-title">Participants ({state.participants.length})</h1>
        <Link to="/participants/new" className="button button--primary">+ Register participant</Link>
      </div>
      {location.state?.message && <p className="success" role="status">{location.state.message}</p>}
      <div className="filters">
        <input type="search" className="input" placeholder="Search name, organization or ID" aria-label="Search participants"
          value={search} onChange={(e) => setSearch(e.target.value)} />
        <select className="input" aria-label="Filter by level" value={level} onChange={(e) => setLevel(e.target.value)}>
          <option value="">All levels</option>
          {EXPERIENCE_LEVELS.map((l) => <option key={l} value={l}>{l}</option>)}
        </select>
      </div>
      {visible.length === 0 ? <p className="muted">No participants match these filters.</p> : (
        <table className="data-table">
          <thead><tr><th>ID</th><th>Name</th><th>Organization</th><th>Level</th><th>Courses</th></tr></thead>
          <tbody>
            {visible.map((p) => (
              <tr key={p.id}>
                <td>{p.id}</td>
                <td><Link to={`/participants/${p.id}`}>{p.name}</Link></td>
                <td>{p.organization}</td>
                <td>{p.level}</td>
                <td>{state.enrollments.filter((e) => e.participantId === p.id).length}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  )
}

export default ParticipantsPage
