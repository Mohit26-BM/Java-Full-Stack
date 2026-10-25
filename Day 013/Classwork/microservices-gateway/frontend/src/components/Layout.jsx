import { Link, NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth.jsx'
import { BankIcon } from './Icons.jsx'

export default function Layout() {
  const { user, isAdmin, logout } = useAuth()
  const navigate = useNavigate()
  const { pathname } = useLocation()

  const handleLogout = () => {
    logout()
    navigate('/login', { replace: true })
  }

  return (
    <>
      <nav className="navbar navbar-expand-md app-navbar shadow-sm">
        <div className="container">
          <Link className="navbar-brand fw-bold" to="/">
            <span className="logo-mark"><BankIcon width={18} height={18} /></span>
            CSB Bank
          </Link>
          <button
            className="navbar-toggler border-0"
            type="button"
            data-bs-toggle="collapse"
            data-bs-target="#mainNav"
            aria-controls="mainNav"
            aria-expanded="false"
            aria-label="Toggle navigation"
          >
            <span className="navbar-toggler-icon" style={{ filter: 'invert(1)' }} />
          </button>
          <div className="collapse navbar-collapse" id="mainNav">
            <ul className="navbar-nav me-auto">
              {isAdmin ? (
                <li className="nav-item">
                  <NavLink className="nav-link" to="/admin/customers">Customers</NavLink>
                </li>
              ) : (
                <li className="nav-item">
                  <NavLink className="nav-link" to="/dashboard">My Accounts</NavLink>
                </li>
              )}
            </ul>
            <div className="d-flex align-items-center gap-3">
              <span className="navbar-text">
                {user.name} <span className="badge badge-role ms-1">{user.role}</span>
              </span>
              <button className="btn btn-sm btn-light-accent" onClick={handleLogout}>Log out</button>
            </div>
          </div>
        </div>
      </nav>
      <main key={pathname} className="container py-4 page-enter">
        <Outlet />
      </main>
    </>
  )
}
