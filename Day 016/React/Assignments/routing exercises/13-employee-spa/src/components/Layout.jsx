import { Link, NavLink, Outlet } from 'react-router-dom'

const linkClass = ({ isActive }) => (isActive ? 'nav__link nav__link--active' : 'nav__link')

function Layout() {
  return (
    <>
      <header className="nav">
        <Link to="/employees" className="nav__brand">Employee Management</Link>
        <nav className="nav__links">
          <NavLink to="/employees" end className={linkClass}>Employees</NavLink>
          <NavLink to="/employees/new" className={linkClass}>Add employee</NavLink>
        </nav>
      </header>
      <main className="app-main">
        <Outlet />
      </main>
    </>
  )
}

export default Layout
