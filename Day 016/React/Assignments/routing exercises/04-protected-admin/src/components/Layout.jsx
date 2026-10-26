import { NavLink, Outlet } from 'react-router-dom'

const linkClass = ({ isActive }) => (isActive ? 'nav__link nav__link--active' : 'nav__link')

function Layout({ loggedIn, onLogout }) {
  return (
    <>
      <header className="nav">
        <span className="nav__brand">Admin Portal</span>
        <nav className="nav__links">
          <NavLink to="/" end className={linkClass}>Home</NavLink>
          <NavLink to="/admin" end className={linkClass}>Dashboard</NavLink>
          <NavLink to="/admin/reports" className={linkClass}>Reports</NavLink>
        </nav>
        <div className="nav__session">
          {loggedIn ? (
            <>
              <span className="muted">Signed in as admin</span>
              <button type="button" className="button" onClick={onLogout}>Logout</button>
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
