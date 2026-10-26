import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useTraining } from '../state/TrainingContext.jsx'

function TrainersPage() {
  const { state } = useTraining()
  const [search, setSearch] = useState('')
  const term = search.trim().toLowerCase()
  const visible = state.trainers.filter((t) =>
    t.name.toLowerCase().includes(term) || t.expertise.toLowerCase().includes(term))

  return (
    <section>
      <h1 className="page-title">Trainers</h1>
      <input type="search" className="input search" placeholder="Search by name or expertise" aria-label="Search trainers"
        value={search} onChange={(e) => setSearch(e.target.value)} />
      {visible.length === 0 ? <p className="muted">No trainers match "{search}".</p> : (
        <div className="cards">
          {visible.map((trainer) => {
            const courses = state.courses.filter((c) => c.trainerId === trainer.id)
            return (
              <article key={trainer.id} className="card">
                <h2 className="card-title">{trainer.name}</h2>
                <p className="muted">{trainer.expertise} · {trainer.email}</p>
                <p className="small">Courses:</p>
                <ul className="plain-list small">
                  {courses.length === 0 ? <li className="muted">none yet</li>
                    : courses.map((c) => <li key={c.id}><Link to={`/courses/${c.id}`}>{c.title}</Link></li>)}
                </ul>
              </article>
            )
          })}
        </div>
      )}
    </section>
  )
}

export default TrainersPage
