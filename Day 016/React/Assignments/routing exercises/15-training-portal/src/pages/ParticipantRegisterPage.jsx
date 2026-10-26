import { Link, useNavigate } from 'react-router-dom'
import Field from '../components/Field.jsx'
import { useValidatedForm } from '../components/useValidatedForm.js'
import { EXPERIENCE_LEVELS } from '../state/sampleData.js'
import { useTraining } from '../state/TrainingContext.jsx'
import { validateParticipant } from '../state/validation.js'

const EMPTY = { name: '', email: '', phone: '', organization: '', level: '' }

function ParticipantRegisterPage() {
  const { state, addParticipant } = useTraining()
  const navigate = useNavigate()
  const form = useValidatedForm(EMPTY, (values) => validateParticipant(values, state.participants))

  const handleSubmit = (event) => {
    event.preventDefault()
    if (!form.isValid) return form.touchAll()
    const v = form.values
    const id = addParticipant({
      name: v.name.trim(), email: v.email.trim().toLowerCase(), phone: v.phone, organization: v.organization.trim(), level: v.level,
    })
    navigate(`/participants/${id}`, { state: { message: `${v.name.trim()} was registered as ${id}.` } })
  }

  return (
    <section className="narrow">
      <h1 className="page-title">Register participant</h1>
      <form className="card form" onSubmit={handleSubmit} noValidate>
        <Field label="Full name" {...form.fieldProps('name')} />
        <Field label="Email" type="email" {...form.fieldProps('email')} />
        <Field label="Mobile number" type="tel" {...form.fieldProps('phone')} />
        <Field label="Organization" {...form.fieldProps('organization')} />
        <Field label="Experience level" options={EXPERIENCE_LEVELS} {...form.fieldProps('level')} />
        <div className="form__actions">
          <button type="submit" className="button button--primary">Register</button>
          <Link to="/participants" className="button">Cancel</Link>
        </div>
      </form>
    </section>
  )
}

export default ParticipantRegisterPage
