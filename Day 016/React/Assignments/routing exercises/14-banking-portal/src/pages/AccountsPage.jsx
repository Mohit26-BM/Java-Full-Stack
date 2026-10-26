import { Link } from 'react-router-dom'
import { rupees } from '../format.js'
import { useBank } from '../state/BankContext.jsx'

function AccountsPage() {
  const { accounts } = useBank()
  return (
    <section>
      <h1 className="page-title">Accounts</h1>
      <table className="data-table">
        <thead>
          <tr><th>Account</th><th>Type</th><th>Name</th><th className="num">Balance</th></tr>
        </thead>
        <tbody>
          {accounts.map((a) => (
            <tr key={a.id}>
              <td><Link to={`/accounts/${a.id}`}>{a.id}</Link></td>
              <td>{a.type}</td>
              <td>{a.name}</td>
              <td className="num">{rupees(a.balance)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  )
}

export default AccountsPage
