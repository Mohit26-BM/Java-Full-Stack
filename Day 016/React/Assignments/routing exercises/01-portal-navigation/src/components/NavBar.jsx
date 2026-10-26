import { NavLink } from 'react-router-dom'
import './NavBar.css'

const LINKS = [
  { to: '/', label: 'Home', end: true },
  { to: '/employees', label: 'Employees' },
  { to: '/departments', label: 'Departments' },
  { to: '/about', label: 'About' },
]

function NavBar() {
  return (
    <header className="navbar">
      <span className="navbar__brand">Employee Portal</span>
      <nav className="navbar__links">
        {LINKS.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            end={link.end}
            className={({ isActive }) => (isActive ? 'navbar__link navbar__link--active' : 'navbar__link')}
          >
            {link.label}
          </NavLink>
        ))}
      </nav>
    </header>
  )
}

export default NavBar
