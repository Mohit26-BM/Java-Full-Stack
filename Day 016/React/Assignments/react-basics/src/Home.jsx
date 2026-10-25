import { Link } from 'react-router-dom'
import { exercises } from './exercises/index.js'

function Home() {
  return (
    <section>
      <h1 className="page-title">Exercises</h1>
      <ol className="exercise-list">
        {exercises.map((exercise) => (
          <li key={exercise.path} className="exercise-list__item">
            <Link to={`/${exercise.path}`} className="exercise-list__link">{exercise.title}</Link>
            <span className="exercise-list__concepts">{exercise.concepts}</span>
          </li>
        ))}
      </ol>
    </section>
  )
}

export default Home
