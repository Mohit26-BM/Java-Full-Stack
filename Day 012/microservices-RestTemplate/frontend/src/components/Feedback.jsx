export function ErrorAlert({ message }) {
  return message ? <div className="alert alert-danger py-2" role="alert">{message}</div> : null
}

export function SuccessAlert({ message }) {
  return message ? <div className="alert alert-success py-2" role="status">{message}</div> : null
}

export function Spinner({ label = 'Loading...' }) {
  return (
    <div className="d-flex align-items-center gap-2 text-secondary py-3">
      <div className="spinner-border spinner-border-sm" role="status" aria-hidden="true" />
      {label}
    </div>
  )
}
