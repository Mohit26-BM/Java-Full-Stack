function CounterDisplay({ count, max }) {
  const isFull = count === max

  return (
    <div className="attendance__display">
      <p className="attendance__count" aria-label="attendance count">{count}</p>
      <p className="attendance__limit">Maximum: {max}</p>
      {isFull && (
        <p className="attendance__warning" role="alert">Maximum attendance of {max} reached.</p>
      )}
    </div>
  )
}

export default CounterDisplay
