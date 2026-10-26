import { Link, useNavigate } from 'react-router-dom'
import Field from '../components/Field.jsx'
import { useValidatedForm } from '../components/useValidatedForm.js'
import { CATEGORIES } from '../state/sampleData.js'
import { useTraining } from '../state/TrainingContext.jsx'
import { validateCourse } from '../state/validation.js'

const EMPTY = { title: '', category: '', trainerId: '', startDate: '', durationDays: '', capacity: '' }

function CourseCreatePage() {
  const { state, addCourse } = useTraining()
  const navigate = useNavigate()
  const form = useValidatedForm(EMPTY, (values) => validateCourse(values, state.courses, state.trainers))

  const handleSubmit = (event) => {
    event.preventDefault()
    if (!form.isValid) return form.touchAll()
    const v = form.values
    const id = addCourse({
      title: v.title.trim(), category: v.category, trainerId: v.trainerId, startDate: v.startDate,
      durationDays: Number(v.durationDays), capacity: Number(v.capacity),
    })
    navigate(`/courses/${id}`, { state: { message: `Course ${id} was created.` } })
  }

  return (
    <section className="narrow">
      <h1 className="page-title">Create course</h1>
      <form className="card form" onSubmit={handleSubmit} noValidate>
        <Field label="Title" {...form.fieldProps('title')} />
        <Field label="Category" options={CATEGORIES} {...form.fieldProps('category')} />
        <Field label="Trainer" options={state.trainers.map((t) => ({ value: t.id, label: `${t.name} — ${t.expertise}` }))}
          {...form.fieldProps('trainerId')} />
        <Field label="Start date" type="date" {...form.fieldProps('startDate')} />
        <Field label="Duration (days)" type="number" min="1" max="90" {...form.fieldProps('durationDays')} />
        <Field label="Capacity (seats)" type="number" min="1" max="100" {...form.fieldProps('capacity')} />
        <div className="form__actions">
          <button type="submit" className="button button--primary">Create course</button>
          <Link to="/courses" className="button">Cancel</Link>
        </div>
      </form>
    </section>
  )
}

export default CourseCreatePage
