import { useReducer } from 'react'
import TransactionForm from './TransactionForm.jsx'
import TransactionHistory from './TransactionHistory.jsx'
import { ACCOUNTS, bankReducer, initialBankState } from './bankReducer.js'
import './BankReducer.css'

const rupees = (n) => `₹${n.toLocaleString('en-IN')}`

function BankReducerApp() {
  const [state, dispatch] = useReducer(bankReducer, initialBankState)

  const submit = (action) => dispatch({ ...action, timestamp: new Date().toISOString() })

  return (
    <section>
      <h1 className="page-title">Bank Transaction Manager</h1>
      <div className="balances">
        {Object.entries(ACCOUNTS).map(([id, name]) => (
          <div key={id} className="card balances__item">
            <p className="muted">{name}</p>
            <p className="balances__value" aria-label={`${name} balance`}>{rupees(state.balances[id])}</p>
          </div>
        ))}
      </div>
      <TransactionForm onSubmit={submit} onReset={() => dispatch({ type: 'RESET' })} />
      {state.error && <p className="error bank-error" role="alert">{state.error}</p>}
      <TransactionHistory transactions={state.transactions} />
    </section>
  )
}

export default BankReducerApp
