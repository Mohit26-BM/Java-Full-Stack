import { useState } from 'react'
import RegistrationForm from './RegistrationForm.jsx'
import SubmittedDetails from './SubmittedDetails.jsx'
import { EMPTY_FORM, validate } from './validate.js'
import './StudentRegistration.css'

function StudentRegistrationApp() {
  const [form, setForm] = useState(EMPTY_FORM)
  const [touched, setTouched] = useState({})
  const [submitted, setSubmitted] = useState(null)

  const errors = validate(form)
  const isValid = Object.keys(errors).length === 0

  const handleChange = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value })
    setSubmitted(null)
  }

  const handleBlur = (event) => {
    setTouched({ ...touched, [event.target.name]: true })
  }

  const handleSubmit = (event) => {
    event.preventDefault()
    if (!isValid) {
      setTouched({ name: true, email: true, age: true, course: true, mobile: true })
      return
    }
    setSubmitted({ ...form, name: form.name.trim(), email: form.email.trim() })
  }

  const handleReset = () => {
    setForm(EMPTY_FORM)
    setTouched({})
    setSubmitted(null)
  }

  return (
    <section className="registration">
      <h1 className="page-title">Student Registration</h1>
      <RegistrationForm
        form={form}
        errors={errors}
        touched={touched}
        isValid={isValid}
        onChange={handleChange}
        onBlur={handleBlur}
        onSubmit={handleSubmit}
        onReset={handleReset}
      />
      {submitted && <SubmittedDetails student={submitted} />}
    </section>
  )
}

export default StudentRegistrationApp
