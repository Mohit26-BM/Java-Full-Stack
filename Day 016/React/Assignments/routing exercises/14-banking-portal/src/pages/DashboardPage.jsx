import { Link } from 'react-router-dom'
import TransactionTable from '../components/TransactionTable.jsx'
import { rupees } from '../format.js'
import { useBank } from '../state/BankContext.jsx'

function DashboardPage() {
  const { user, accounts, transactions } = useBank()
  const total = accounts.reduce((sum, a) => sum + a.balance, 0)

  return (
    <section>
      <h1 className="page-title">Welcome, {user.name}</h1>
      <div className="tiles">
        <div className="card tile tile--accent">
          <p className="muted">Total balance</p>
          <p className="tile__value" aria-label="Total balance">{rupees(total)}</p>
        </div>
        {accounts.map((account) => (
          <Link key={account.id} to={`/accounts/${account.id}`} className="card tile tile--link">
            <p className="muted">{account.name} · {account.id}</p>
            <p className="tile__value">{rupees(account.balance)}</p>
          </Link>
        ))}
      </div>
      <div className="page-head">
        <h2 className="section-title">Recent transactions</h2>
        <Link to="/transfer" className="button button--primary">Transfer money</Link>
      </div>
      <TransactionTable transactions={transactions.slice(0, 5)} />
    </section>
  )
}

export default DashboardPage
