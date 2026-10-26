import { Link, useLocation } from 'react-router-dom'

function NotFoundPage() {
  const { pathname } = useLocation()
  return (
    <section>
      <h1 className="page-title">404 — Page Not Found</h1>
      <p className="muted">There is no page at <code>{pathname}</code>.</p>
      <Link to="/">Go home</Link>
    </section>
  )
}

export default NotFoundPage
