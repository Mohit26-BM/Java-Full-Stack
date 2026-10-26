import { Link, Route, Routes } from 'react-router-dom'
import { exercises } from './exercises/index.js'

function Menu() {
  return (
    <section>
      <h1 className="page-title">Forms and State Management</h1>
      <ol className="menu">
        {exercises.map((exercise) => (
          <li key={exercise.path} className="card menu__item">
            <Link to={`/${exercise.path}`} className="menu__link">{exercise.number}. {exercise.title}</Link>
            <span className="muted">{exercise.concepts}</span>
          </li>
        ))}
      </ol>
    </section>
  )
}

function App() {
  return (
    <>
      <header className="app-header">
        <Link to="/" className="app-title">Forms and State Management</Link>
      </header>
      <main className="app-main">
        <Routes>
          <Route path="/" element={<Menu />} />
          {exercises.map(({ path, Component }) => (
            <Route key={path} path={`/${path}/*`} element={<Component />} />
          ))}
          <Route path="*" element={<p>Page not found. <Link to="/">Back to the list</Link></p>} />
        </Routes>
      </main>
    </>
  )
}

export default App
