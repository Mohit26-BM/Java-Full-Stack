function CounterControls({ count, max, onChange, onReset }) {
  return (
    <div className="attendance__controls">
      <button type="button" className="attendance__button" onClick={() => onChange(-5)} disabled={count === 0}>-5</button>
      <button type="button" className="attendance__button" onClick={() => onChange(-1)} disabled={count === 0}>Decrement</button>
      <button type="button" className="attendance__button attendance__button--primary" onClick={() => onChange(1)} disabled={count === max}>Increment</button>
      <button type="button" className="attendance__button" onClick={() => onChange(5)} disabled={count === max}>+5</button>
      <button type="button" className="attendance__button attendance__button--reset" onClick={onReset}>Reset</button>
    </div>
  )
}

export default CounterControls
