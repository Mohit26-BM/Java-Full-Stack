import { Link, Route, Routes } from 'react-router-dom'
import { exercises } from './exercises/index.js'
import Home from './Home.jsx'

function App() {
  return (
    <div className="app-shell">
      <header className="app-header">
        <Link to="/" className="app-title">React Basics</Link>
      </header>
      <main className="app-main">
        <Routes>
          <Route path="/" element={<Home />} />
          {exercises.map(({ path, Component }) => (
            <Route key={path} path={`/${path}`} element={<Component />} />
          ))}
          <Route path="*" element={<p>Page not found. <Link to="/">Back to the list</Link></p>} />
        </Routes>
      </main>
    </div>
  )
}

export default App
