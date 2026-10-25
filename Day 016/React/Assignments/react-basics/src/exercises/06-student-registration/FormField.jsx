function FormField({ label, name, error, children }) {
  return (
    <div className="registration__field">
      <label htmlFor={name} className="registration__label">{label}</label>
      {children}
      {error && <p id={`${name}-error`} className="registration__error">{error}</p>}
    </div>
  )
}

export default FormField
