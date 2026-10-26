import { useState } from 'react'
import { useTraining } from '../state/TrainingContext.jsx'
import { enrolledCount, enrollmentProblem } from '../state/trainingReducer.js'

function EnrollForm({ participantId: fixedParticipant, courseId: fixedCourse }) {
  const { state, enroll } = useTraining()
  const [participantId, setParticipantId] = useState(fixedParticipant ?? '')
  const [courseId, setCourseId] = useState(fixedCourse ?? '')
  const [message, setMessage] = useState({ text: '', ok: false })

  const handleSubmit = (event) => {
    event.preventDefault()
    const problem = enrollmentProblem(state, participantId, courseId)
    if (problem) return setMessage({ text: problem, ok: false })
    enroll(participantId, courseId)
    setMessage({ text: 'Enrolled successfully.', ok: true })
    if (!fixedParticipant) setParticipantId('')
    if (!fixedCourse) setCourseId('')
  }

  return (
    <form className="card enroll-form" onSubmit={handleSubmit} noValidate aria-label="Enroll">
      {!fixedParticipant && (
        <label className="field">
          Participant
          <select className="input" value={participantId} onChange={(e) => setParticipantId(e.target.value)}>
            <option value="">Select</option>
            {state.participants.map((p) => <option key={p.id} value={p.id}>{p.name} ({p.id})</option>)}
          </select>
        </label>
      )}
      {!fixedCourse && (
        <label className="field">
          Course
          <select className="input" value={courseId} onChange={(e) => setCourseId(e.target.value)}>
            <option value="">Select</option>
            {state.courses.map((c) => {
              const left = c.capacity - enrolledCount(state.enrollments, c.id)
              return <option key={c.id} value={c.id}>{c.title} — {left > 0 ? `${left} seats left` : 'full'}</option>
            })}
          </select>
        </label>
      )}
      <button type="submit" className="button button--primary">Enroll</button>
      {message.text && <p className={message.ok ? 'success' : 'error'} role={message.ok ? 'status' : 'alert'}>{message.text}</p>}
    </form>
  )
}

export default EnrollForm
