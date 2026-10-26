function ProgressIndicator({ current, total, titles }) {
  return (
    <div className="progress">
      <p className="progress__label">
        Step {current} of {total}: <strong>{titles[current - 1]}</strong>
      </p>
      <div className="progress__bar" role="progressbar" aria-valuemin={1} aria-valuemax={total} aria-valuenow={current}
        aria-label="Application progress">
        <div className="progress__fill" style={{ width: `${(current / total) * 100}%` }} />
      </div>
    </div>
  )
}

export default ProgressIndicator
