import { useState } from 'react'
import { ACCOUNTS } from './bankReducer.js'

const TYPES = ['DEPOSIT', 'WITHDRAW', 'TRANSFER']

function TransactionForm({ onSubmit, onReset }) {
  const [type, setType] = useState('DEPOSIT')
  const [account, setAccount] = useState('savings')
  const [to, setTo] = useState('current')
  const [amount, setAmount] = useState('')

  const handleSubmit = (event) => {
    event.preventDefault()
    const value = Number(amount)
    onSubmit(type === 'TRANSFER'
      ? { type, from: account, to, amount: value }
      : { type, account, amount: value })
    setAmount('')
  }

  const accountOptions = Object.entries(ACCOUNTS).map(([id, name]) => <option key={id} value={id}>{name}</option>)

  return (
    <form className="card tx-form" onSubmit={handleSubmit} noValidate>
      <label className="field">
        Transaction
        <select className="input" value={type} onChange={(e) => setType(e.target.value)}>
          {TYPES.map((t) => <option key={t} value={t}>{t}</option>)}
        </select>
      </label>
      <label className="field">
        {type === 'TRANSFER' ? 'From account' : 'Account'}
        <select className="input" value={account} onChange={(e) => setAccount(e.target.value)}>{accountOptions}</select>
      </label>
      {type === 'TRANSFER' && (
        <label className="field">
          To account
          <select className="input" value={to} onChange={(e) => setTo(e.target.value)}>{accountOptions}</select>
        </label>
      )}
      <label className="field">
        Amount (₹)
        <input className="input" type="number" min="1" value={amount} onChange={(e) => setAmount(e.target.value)} />
      </label>
      <div className="form__actions tx-form__actions">
        <button type="submit" className="button button--primary">Submit</button>
        <button type="button" className="button" onClick={onReset}>Reset</button>
      </div>
    </form>
  )
}

export default TransactionForm
