import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { rupees } from '../format.js'
import { PAYEES } from '../state/bankReducer.js'
import { useBank } from '../state/BankContext.jsx'
import { REMARK_LIMIT, validateTransfer } from '../state/validateTransfer.js'

const EMPTY = { fromAccountId: '', toId: '', amount: '', remark: '' }

function TransferPage() {
  const { accounts, pendingTransfer, prepareTransfer } = useBank()
  const navigate = useNavigate()

  const [form, setForm] = useState(pendingTransfer
    ? { ...pendingTransfer.form }
    : EMPTY)
  const [showErrors, setShowErrors] = useState(false)

  const errors = validateTransfer(form, accounts)
  const shown = showErrors ? errors : {}
  const setField = (name, value) => setForm({ ...form, [name]: value })

  const handleSubmit = (event) => {
    event.preventDefault()
    if (Object.keys(errors).length > 0) {
      setShowErrors(true)
      return
    }
    const ownTarget = accounts.find((a) => a.id === form.toId)
    const payee = PAYEES.find((p) => p.id === form.toId)
    prepareTransfer({
      form,
      fromAccountId: form.fromAccountId,
      toId: form.toId,
      toLabel: ownTarget ? `${ownTarget.name} (${ownTarget.id})` : `${payee.name}, ${payee.bank} ${payee.accountNumber}`,
      amount: Number(form.amount),
      remark: form.remark.trim(),
    })
    navigate('/transfer/confirm')
  }

  const field = (name, label, control) => (
    <div className="field">
      <label htmlFor={name}>{label}</label>
      {control}
      {shown[name] && <p id={`${name}-error`} className="error">{shown[name]}</p>}
    </div>
  )

  return (
    <section className="narrow">
      <h1 className="page-title">Transfer money</h1>
      <form className="card form" onSubmit={handleSubmit} noValidate>
        {field('fromAccountId', 'From account',
          <select id="fromAccountId" className="input" value={form.fromAccountId} onChange={(e) => setField('fromAccountId', e.target.value)}>
            <option value="">Select</option>
            {accounts.map((a) => <option key={a.id} value={a.id}>{a.name} ({a.id}) — {rupees(a.balance)}</option>)}
          </select>)}
        {field('toId', 'To',
          <select id="toId" className="input" value={form.toId} onChange={(e) => setField('toId', e.target.value)}>
            <option value="">Select</option>
            <optgroup label="My accounts">
              {accounts.map((a) => <option key={a.id} value={a.id}>{a.name} ({a.id})</option>)}
            </optgroup>
            <optgroup label="Saved payees">
              {PAYEES.map((p) => <option key={p.id} value={p.id}>{p.name} — {p.bank} {p.accountNumber}</option>)}
            </optgroup>
          </select>)}
        {field('amount', 'Amount (₹)',
          <input id="amount" type="number" min="1" step="0.01" className="input" value={form.amount}
            onChange={(e) => setField('amount', e.target.value)} />)}
        {field('remark', `Remark (optional, max ${REMARK_LIMIT})`,
          <input id="remark" className="input" value={form.remark} onChange={(e) => setField('remark', e.target.value)} />)}
        <div className="form__actions">
          <button type="submit" className="button button--primary">Continue</button>
        </div>
      </form>
    </section>
  )
}

export default TransferPage
