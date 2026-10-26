import FormField from '../../shared/FormField.jsx'

function StepFields({ step, application, errors, onChange }) {
  return (
    <fieldset className="loan__fields">
      <legend className="loan__legend">{step.title}</legend>
      {step.fields.map((field) => (
        <FormField key={field.name} id={field.name} label={field.label} error={errors[field.name]}>
          {field.options ? (
            <select id={field.name} className="input" value={application[field.name]}
              onChange={(e) => onChange(field.name, e.target.value)}>
              <option value="">Select</option>
              {field.options.map((o) => <option key={o} value={o}>{o}</option>)}
            </select>
          ) : (
            <input id={field.name} type={field.type ?? 'text'} className="input" value={application[field.name]}
              onChange={(e) => onChange(field.name, e.target.value)} />
          )}
        </FormField>
      ))}
    </fieldset>
  )
}

export default StepFields
