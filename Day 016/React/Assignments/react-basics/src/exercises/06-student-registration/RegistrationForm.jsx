import FormField from './FormField.jsx'
import { COURSES } from './validate.js'

function RegistrationForm({ form, errors, touched, isValid, onChange, onBlur, onSubmit, onReset }) {

  const errorFor = (name) => (touched[name] ? errors[name] : undefined)
  const inputProps = (name) => ({
    id: name,
    name,
    value: form[name],
    onChange,
    onBlur,
    className: errorFor(name) ? 'registration__input registration__input--invalid' : 'registration__input',
    'aria-invalid': Boolean(errorFor(name)),
    'aria-describedby': errorFor(name) ? `${name}-error` : undefined,
  })

  return (
    <form className="registration__form" onSubmit={onSubmit} noValidate>
      <FormField label="Name" name="name" error={errorFor('name')}>
        <input type="text" {...inputProps('name')} />
      </FormField>
      <FormField label="Email" name="email" error={errorFor('email')}>
        <input type="email" {...inputProps('email')} />
      </FormField>
      <FormField label="Age" name="age" error={errorFor('age')}>
        <input type="number" min="18" max="60" {...inputProps('age')} />
      </FormField>
      <FormField label="Course" name="course" error={errorFor('course')}>
        <select {...inputProps('course')}>
          <option value="">Select a course</option>
          {COURSES.map((course) => <option key={course} value={course}>{course}</option>)}
        </select>
      </FormField>
      <FormField label="Mobile Number" name="mobile" error={errorFor('mobile')}>
        <input type="tel" inputMode="numeric" maxLength={10} {...inputProps('mobile')} />
      </FormField>
      <div className="registration__actions">
        <button type="submit" className="registration__button registration__button--primary" disabled={!isValid}>Submit</button>
        <button type="button" className="registration__button" onClick={onReset}>Reset</button>
      </div>
    </form>
  )
}

export default RegistrationForm
