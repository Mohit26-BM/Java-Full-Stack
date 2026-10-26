import { useState } from 'react'
import FormField from '../../shared/FormField.jsx'
import AccountTypeFields from './AccountTypeFields.jsx'
import NomineeList from './NomineeList.jsx'
import { ACCOUNT_TYPES, initialForm, newNominee, toSubmission, validateBankForm } from './bankForm.js'
import './BankAccountForm.css'

function BankAccountFormApp() {
  const [form, setForm] = useState(initialForm)
  const [showErrors, setShowErrors] = useState(false)
  const [submitted, setSubmitted] = useState(null)

  const errors = validateBankForm(form)
  const shown = showErrors ? errors : {}

  const setField = (name, value) => {
    setForm({ ...form, [name]: value })
    setSubmitted(null)
  }

  const addNominee = () => setForm({ ...form, nominees: [...form.nominees, newNominee()] })
  const removeNominee = (id) => setForm({ ...form, nominees: form.nominees.filter((n) => n.id !== id) })
  const changeNominee = (id, field, value) => setForm({
    ...form,
    nominees: form.nominees.map((n) => (n.id === id ? { ...n, [field]: value } : n)),
  })

  const handleSubmit = (event) => {
    event.preventDefault()
    if (Object.keys(errors).length > 0) {
      setShowErrors(true)
      return
    }
    setSubmitted(toSubmission(form))
    setForm(initialForm())
    setShowErrors(false)
  }

  return (
    <section>
      <h1 className="page-title">Bank Customer Registration</h1>
      <div className="bank">
        <form className="card form" onSubmit={handleSubmit} noValidate>
          <FormField id="customerName" label="Customer name" error={shown.customerName}>
            <input id="customerName" className="input" value={form.customerName}
              onChange={(e) => setField('customerName', e.target.value)} />
          </FormField>
          <FormField id="accountType" label="Account type" error={shown.accountType}>
            <select id="accountType" className="input" value={form.accountType}
              onChange={(e) => setField('accountType', e.target.value)}>
              <option value="">Select an account type</option>
              {ACCOUNT_TYPES.map((t) => <option key={t} value={t}>{t}</option>)}
            </select>
          </FormField>
          <FormField id="initialDeposit" label="Initial deposit (₹)" error={shown.initialDeposit}>
            <input id="initialDeposit" type="number" min="0" className="input" value={form.initialDeposit}
              onChange={(e) => setField('initialDeposit', e.target.value)} />
          </FormField>

          <AccountTypeFields form={form} errors={shown} onChange={setField} />

          <NomineeList
            nominees={form.nominees}
            errors={shown}
            onAdd={addNominee}
            onRemove={removeNominee}
            onChange={changeNominee}
          />

          {showErrors && Object.keys(errors).length > 0 && (
            <p className="error" role="alert">Please correct the highlighted fields.</p>
          )}
          <div className="form__actions">
            <button type="submit" className="button button--primary">Register customer</button>
          </div>
          {submitted && (
            <p className="success" role="status">
              {submitted.accountType} account opened for {submitted.customerName} with {submitted.nominees.length} nominee(s).
            </p>
          )}
        </form>

        <aside className="card bank__summary" aria-label="Live summary">
          <h2 className="section-title bank__summary-title">Live summary</h2>
          <pre className="bank__json">{JSON.stringify(toSubmission(form), null, 2)}</pre>
        </aside>
      </div>
    </section>
  )
}

export default BankAccountFormApp
