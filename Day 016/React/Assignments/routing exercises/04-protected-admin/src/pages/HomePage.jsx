import { Link } from 'react-router-dom'

function HomePage() {
  return (
    <section>
      <h1 className="page-title">Welcome</h1>
      <p>This page is public. The <Link to="/admin">Admin Dashboard</Link> and <Link to="/admin/reports">Reports</Link> need a login.</p>
    </section>
  )
}

export default HomePage
