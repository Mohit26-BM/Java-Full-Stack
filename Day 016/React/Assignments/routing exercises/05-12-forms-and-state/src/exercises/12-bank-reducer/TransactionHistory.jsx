import { ACCOUNTS } from './bankReducer.js'

const describe = (t) => (t.type === 'TRANSFER' ? `${ACCOUNTS[t.from]} → ${ACCOUNTS[t.to]}` : ACCOUNTS[t.account])

function TransactionHistory({ transactions }) {
  return (
    <>
      <h2 className="section-title">Transaction history ({transactions.length})</h2>
      {transactions.length === 0 ? (
        <p className="muted">No transactions yet.</p>
      ) : (
        <table className="data-table">
          <thead>
            <tr><th>#</th><th>Type</th><th>Account</th><th>Amount</th><th>Time</th></tr>
          </thead>
          <tbody>
            {transactions.map((t) => (
              <tr key={t.id}>
                <td>{t.id}</td>
                <td>{t.type}</td>
                <td>{describe(t)}</td>
                <td>₹{t.amount.toLocaleString('en-IN')}</td>
                <td>{new Date(t.timestamp).toLocaleString('en-IN')}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </>
  )
}

export default TransactionHistory
