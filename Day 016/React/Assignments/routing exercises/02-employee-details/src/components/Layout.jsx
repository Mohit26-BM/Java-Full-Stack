import { Link, Outlet } from 'react-router-dom'

function Layout() {
  return (
    <>
      <header className="app-header">
        <Link to="/employees" className="app-title">Employee Directory</Link>
      </header>
      <main className="app-main">
        <Outlet />
      </main>
    </>
  )
}

export default Layout
