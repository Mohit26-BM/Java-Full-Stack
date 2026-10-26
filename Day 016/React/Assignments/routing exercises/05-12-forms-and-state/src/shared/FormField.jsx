function FormField({ id, label, error, children }) {
  return (
    <div className="field">
      <label htmlFor={id}>{label}</label>
      {children}
      {error && <p id={`${id}-error`} className="error">{error}</p>}
    </div>
  )
}

export default FormField
