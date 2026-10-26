import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useBank } from '../state/BankContext.jsx'

const LINKS = [
  ['/dashboard', 'Dashboard'],
  ['/accounts', 'Accounts'],
  ['/transactions', 'Transactions'],
  ['/transfer', 'Transfer'],
  ['/profile', 'Profile'],
]

const linkClass = ({ isActive }) => (isActive ? 'nav__link nav__link--active' : 'nav__link')

function Layout() {
  const { user, logout } = useBank()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login', { replace: true })
  }

  return (
    <>
      <header className="nav">
        <span className="nav__brand">🏦 Banking Portal</span>
        {user && (
          <nav className="nav__links">
            {LINKS.map(([to, label]) => <NavLink key={to} to={to} className={linkClass}>{label}</NavLink>)}
          </nav>
        )}
        <div className="nav__session">
          {user ? (
            <>
              <span className="muted">{user.name}</span>
              <button type="button" className="button" onClick={handleLogout}>Logout</button>
            </>
          ) : (
            <NavLink to="/login" className={linkClass}>Login</NavLink>
          )}
        </div>
      </header>
      <main className="app-main">
        <Outlet />
      </main>
    </>
  )
}

export default Layout
