import { useState } from 'react'
import ProgressIndicator from './ProgressIndicator.jsx'
import ReviewStep from './ReviewStep.jsx'
import StepFields from './StepFields.jsx'
import { EMPTY_APPLICATION, STEPS, TOTAL_STEPS } from './loanSteps.js'
import './LoanApplication.css'

const REVIEW = STEPS.length

function LoanApplicationApp() {
  const [application, setApplication] = useState(EMPTY_APPLICATION)
  const [step, setStep] = useState(0)
  const [showErrors, setShowErrors] = useState(false)
  const [editingFromReview, setEditingFromReview] = useState(false)
  const [submitted, setSubmitted] = useState(false)

  const errors = step < REVIEW ? STEPS[step].validate(application) : {}
  const stepIsValid = Object.keys(errors).length === 0

  const handleChange = (name, value) => setApplication({ ...application, [name]: value })

  const goTo = (target) => {
    setStep(target)
    setShowErrors(false)
  }

  const next = () => {
    if (!stepIsValid) {
      setShowErrors(true)
      return
    }
    goTo(editingFromReview ? REVIEW : step + 1)
    setEditingFromReview(false)
  }

  const editSection = (index) => {
    setEditingFromReview(true)
    goTo(index)
  }

  if (submitted) {
    return (
      <section>
        <h1 className="page-title">Loan Application</h1>
        <div className="card">
          <p className="success" role="status">
            Application submitted: {application.loanType} loan of ₹{Number(application.amount).toLocaleString('en-IN')} for {application.fullName}.
          </p>
          <button type="button" className="button loan__restart" onClick={() => {
            setApplication(EMPTY_APPLICATION)
            setSubmitted(false)
            goTo(0)
          }}>Start a new application</button>
        </div>
      </section>
    )
  }

  return (
    <section>
      <h1 className="page-title">Loan Application</h1>
      <ProgressIndicator current={step + 1} total={TOTAL_STEPS} titles={[...STEPS.map((s) => s.title), 'Review and submit']} />
      <div className="card loan">
        {step < REVIEW ? (
          <StepFields step={STEPS[step]} application={application} errors={showErrors ? errors : {}} onChange={handleChange} />
        ) : (
          <ReviewStep application={application} steps={STEPS} onEdit={editSection} />
        )}
        <div className="form__actions loan__nav">
          <button type="button" className="button" onClick={() => goTo(step - 1)} disabled={step === 0 || editingFromReview}>
            Back
          </button>
          {step < REVIEW ? (
            <button type="button" className="button button--primary" onClick={next}>
              {editingFromReview ? 'Save and return to review' : 'Next'}
            </button>
          ) : (
            <button type="button" className="button button--primary" onClick={() => setSubmitted(true)}>Submit application</button>
          )}
        </div>
      </div>
    </section>
  )
}

export default LoanApplicationApp
