import { Outlet } from 'react-router-dom'
import NavBar from './NavBar.jsx'

function Layout() {
  return (
    <>
      <NavBar />
      <main className="app-main">
        <Outlet />
      </main>
    </>
  )
}

export default Layout
