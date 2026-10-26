import { NavLink, Outlet } from 'react-router-dom'

const LINKS = [
  ['/dashboard', 'Dashboard'],
  ['/trainers', 'Trainers'],
  ['/participants', 'Participants'],
  ['/courses', 'Courses'],
  ['/enrollments', 'Enrollments'],
  ['/reports', 'Reports'],
]

const linkClass = ({ isActive }) => (isActive ? 'nav__link nav__link--active' : 'nav__link')

function Layout() {
  return (
    <>
      <header className="nav">
        <span className="nav__brand">🎓 Training Portal</span>
        <nav className="nav__links">
          {LINKS.map(([to, label]) => <NavLink key={to} to={to} className={linkClass}>{label}</NavLink>)}
        </nav>
      </header>
      <main className="app-main">
        <Outlet />
      </main>
    </>
  )
}

export default Layout
