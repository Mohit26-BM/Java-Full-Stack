import { useState } from 'react'
import { Link } from 'react-router-dom'
import { CATEGORIES } from '../state/sampleData.js'
import { useTraining } from '../state/TrainingContext.jsx'
import { enrolledCount } from '../state/trainingReducer.js'

function CoursesPage() {
  const { state, trainerById } = useTraining()
  const [search, setSearch] = useState('')
  const [category, setCategory] = useState('')

  const term = search.trim().toLowerCase()
  const visible = state.courses.filter((c) =>
    (c.title.toLowerCase().includes(term) || (trainerById(c.trainerId)?.name ?? '').toLowerCase().includes(term))
    && (!category || c.category === category))

  return (
    <section>
      <div className="page-head">
        <h1 className="page-title">Courses ({state.courses.length})</h1>
        <Link to="/courses/new" className="button button--primary">+ Create course</Link>
      </div>
      <div className="filters">
        <input type="search" className="input" placeholder="Search title or trainer" aria-label="Search courses"
          value={search} onChange={(e) => setSearch(e.target.value)} />
        <select className="input" aria-label="Filter by category" value={category} onChange={(e) => setCategory(e.target.value)}>
          <option value="">All categories</option>
          {CATEGORIES.map((c) => <option key={c} value={c}>{c}</option>)}
        </select>
      </div>
      {visible.length === 0 ? <p className="muted">No courses match these filters.</p> : (
        <table className="data-table">
          <thead><tr><th>ID</th><th>Title</th><th>Category</th><th>Trainer</th><th>Starts</th><th>Seats</th></tr></thead>
          <tbody>
            {visible.map((c) => (
              <tr key={c.id}>
                <td>{c.id}</td>
                <td><Link to={`/courses/${c.id}`}>{c.title}</Link></td>
                <td>{c.category}</td>
                <td>{trainerById(c.trainerId)?.name}</td>
                <td>{c.startDate}</td>
                <td>{enrolledCount(state.enrollments, c.id)} / {c.capacity}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  )
}

export default CoursesPage
