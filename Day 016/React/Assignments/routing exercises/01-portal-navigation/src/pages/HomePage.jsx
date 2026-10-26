import { Link } from 'react-router-dom'

function HomePage() {
  return (
    <section>
      <h1 className="page-title">Welcome to the Employee Portal</h1>
      <p className="muted">Find colleagues and learn about our departments.</p>
      <div className="home-links">
        <Link to="/employees" className="card home-links__item">View employees →</Link>
        <Link to="/departments" className="card home-links__item">View departments →</Link>
      </div>
    </section>
  )
}

export default HomePage
