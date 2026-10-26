function Field({ name, label, value, onChange, onBlur, error, type = 'text', options, ...rest }) {
  const common = {
    id: name,
    name,
    value,
    onChange,
    onBlur,
    className: error ? 'input input--invalid' : 'input',
    'aria-invalid': Boolean(error),
    'aria-describedby': error ? `${name}-error` : undefined,
    ...rest,
  }
  return (
    <div className="field">
      <label htmlFor={name}>{label}</label>
      {options ? (
        <select {...common}>
          <option value="">Select</option>
          {options.map((o) => (typeof o === 'string'
            ? <option key={o} value={o}>{o}</option>
            : <option key={o.value} value={o.value}>{o.label}</option>))}
        </select>
      ) : (
        <input type={type} {...common} />
      )}
      {error && <p id={`${name}-error`} className="error">{error}</p>}
    </div>
  )
}

export default Field
