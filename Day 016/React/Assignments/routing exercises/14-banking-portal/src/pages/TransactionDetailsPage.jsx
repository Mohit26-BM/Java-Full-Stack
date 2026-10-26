import { Link, useLocation, useParams } from 'react-router-dom'
import { dateTime, rupees } from '../format.js'
import { useBank } from '../state/BankContext.jsx'

function TransactionDetailsPage() {
  const { transactionId } = useParams()
  const { state } = useLocation()
  const { findTransaction } = useBank()
  const transaction = findTransaction(transactionId)

  if (!transaction) {
    return (
      <section className="card narrow">
        <h1 className="page-title">Transaction not found</h1>
        <p className="muted">There is no transaction <code>{transactionId}</code>.</p>
        <Link to="/transactions">Back to transactions</Link>
      </section>
    )
  }

  return (
    <section className="card narrow">
      {state?.message && <p className="success" role="status">{state.message}</p>}
      <h1 className="page-title">Transaction {transaction.id}</h1>
      <dl className="details">
        <dt>Date</dt><dd>{dateTime(transaction.date)}</dd>
        <dt>Type</dt><dd>{transaction.kind === 'CREDIT' ? 'Credit (money in)' : 'Debit (money out)'}</dd>
        <dt>Account</dt><dd><Link to={`/accounts/${transaction.accountId}`}>{transaction.accountId}</Link></dd>
        <dt>Amount</dt><dd className="strong">{rupees(transaction.amount)}</dd>
        <dt>Description</dt><dd>{transaction.description}</dd>
      </dl>
      <Link to="/transactions">Back to transactions</Link>
    </section>
  )
}

export default TransactionDetailsPage
