import { BankIcon, BoltIcon, ShieldIcon, WalletIcon } from './Icons.jsx'

/** Split-screen layout shared by the login and register pages. */
export default function AuthShell({ children, wide = false }) {
  return (
    <div className="auth-split">
      <aside className="auth-brand">
        <span className="blob b1" />
        <span className="blob b2" />
        <span className="blob b3" />
        <span className="logo-mark mb-4"><BankIcon width={26} height={26} /></span>
        <h2 className="mb-3">Banking that keeps up with you.</h2>
        <p className="lead">Manage savings accounts and loans in one place, securely and in real time.</p>
        <ul className="auth-points">
          <li><span className="dot"><ShieldIcon /></span>Secure, token-based sign-in</li>
          <li><span className="dot"><WalletIcon /></span>Savings accounts and loans at a glance</li>
          <li><span className="dot"><BoltIcon /></span>Instant updates across services</li>
        </ul>
      </aside>
      <section className="auth-panel">
        <div className={`auth-card${wide ? ' wide' : ''}`}>{children}</div>
      </section>
    </div>
  )
}
