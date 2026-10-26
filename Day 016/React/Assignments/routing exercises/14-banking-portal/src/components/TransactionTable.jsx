import { Link } from 'react-router-dom'
import { dateTime, rupees } from '../format.js'

function TransactionTable({ transactions, emptyText = 'No transactions.' }) {
  if (transactions.length === 0) return <p className="muted">{emptyText}</p>
  return (
    <table className="data-table">
      <thead>
        <tr><th>ID</th><th>Date</th><th>Description</th><th>Account</th><th className="num">Amount</th></tr>
      </thead>
      <tbody>
        {transactions.map((t) => (
          <tr key={t.id}>
            <td><Link to={`/transactions/${t.id}`}>{t.id}</Link></td>
            <td>{dateTime(t.date)}</td>
            <td>{t.description}</td>
            <td>{t.accountId}</td>
            <td className={t.kind === 'CREDIT' ? 'num credit' : 'num debit'}>
              {t.kind === 'CREDIT' ? '+' : '−'} {rupees(t.amount)}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}

export default TransactionTable
