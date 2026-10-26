import { useState } from 'react'
import FormField from '../../shared/FormField.jsx'
import { firstError, onlyErrors, required } from '../../shared/validators.js'

export function validateStudent({ name, marks }) {
  const n = Number(marks)
  return onlyErrors({
    name: required(name, 'Student name'),
    marks: firstError(
      required(marks, 'Marks'),
      Number.isInteger(n) && n >= 0 && n <= 100 ? '' : 'Marks must be a whole number from 0 to 100',
    ),
  })
}

function StudentForm({ onAdd }) {
  const [name, setName] = useState('')
  const [marks, setMarks] = useState('')
  const [errors, setErrors] = useState({})

  const handleSubmit = (event) => {
    event.preventDefault()
    const found = validateStudent({ name, marks })
    setErrors(found)
    if (Object.keys(found).length > 0) return
    onAdd({ name: name.trim(), marks: Number(marks) })
    setName('')
    setMarks('')
  }

  return (
    <form className="card form" onSubmit={handleSubmit} noValidate>
      <FormField id="student-name" label="Student name" error={errors.name}>
        <input id="student-name" className="input" value={name} onChange={(e) => setName(e.target.value)} />
      </FormField>
      <FormField id="student-marks" label="Marks (0–100)" error={errors.marks}>
        <input id="student-marks" type="number" min="0" max="100" className="input" value={marks}
          onChange={(e) => setMarks(e.target.value)} />
      </FormField>
      <div className="form__actions">
        <button type="submit" className="button button--primary">Add student</button>
      </div>
    </form>
  )
}

export default StudentForm
