import { useState } from 'react'
import TransactionTable from '../components/TransactionTable.jsx'
import { useBank } from '../state/BankContext.jsx'

function TransactionsPage() {
  const { accounts, transactions } = useBank()
  const [accountId, setAccountId] = useState('ALL')
  const visible = accountId === 'ALL' ? transactions : transactions.filter((t) => t.accountId === accountId)

  return (
    <section>
      <div className="page-head">
        <h1 className="page-title">Transactions ({visible.length})</h1>
        <label className="field inline-field">
          Account
          <select className="input" value={accountId} onChange={(e) => setAccountId(e.target.value)}>
            <option value="ALL">All accounts</option>
            {accounts.map((a) => <option key={a.id} value={a.id}>{a.id} · {a.name}</option>)}
          </select>
        </label>
      </div>
      <TransactionTable transactions={visible} />
    </section>
  )
}

export default TransactionsPage
