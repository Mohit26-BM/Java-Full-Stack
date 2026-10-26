import { Link, useParams } from 'react-router-dom'
import TransactionTable from '../components/TransactionTable.jsx'
import { rupees } from '../format.js'
import { useBank } from '../state/BankContext.jsx'

function AccountDetailsPage() {
  const { accountId } = useParams()
  const { findAccount, transactions } = useBank()
  const account = findAccount(accountId)

  if (!account) {
    return (
      <section className="card narrow">
        <h1 className="page-title">Account not found</h1>
        <p className="muted">You have no account <code>{accountId}</code>.</p>
        <Link to="/accounts">Back to accounts</Link>
      </section>
    )
  }

  return (
    <section>
      <h1 className="page-title">{account.name}</h1>
      <div className="card summary-card">
        <dl className="details">
          <dt>Account number</dt><dd>{account.id}</dd>
          <dt>Type</dt><dd>{account.type}</dd>
          <dt>Balance</dt><dd className="strong" aria-label="Account balance">{rupees(account.balance)}</dd>
        </dl>
      </div>
      <h2 className="section-title">Transactions on this account</h2>
      <TransactionTable transactions={transactions.filter((t) => t.accountId === account.id)}
        emptyText="No transactions on this account yet." />
    </section>
  )
}

export default AccountDetailsPage
