import { Link } from 'react-router-dom'

function NotFoundCard({ what, id, backTo, backLabel }) {
  return (
    <section className="card narrow">
      <h1 className="page-title">{what} not found</h1>
      <p className="muted">There is no {what.toLowerCase()} with the ID <code>{id}</code>.</p>
      <Link to={backTo}>{backLabel}</Link>
    </section>
  )
}

export default NotFoundCard
