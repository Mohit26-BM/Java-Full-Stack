import { useState } from 'react'
import { Route, Routes, useNavigate } from 'react-router-dom'
import Layout from './components/Layout.jsx'
import RequireAuth from './components/RequireAuth.jsx'
import AdminDashboardPage from './pages/AdminDashboardPage.jsx'
import AdminReportsPage from './pages/AdminReportsPage.jsx'
import HomePage from './pages/HomePage.jsx'
import LoginPage from './pages/LoginPage.jsx'
import NotFoundPage from './pages/NotFoundPage.jsx'

export const DEMO_USER = { username: 'admin', password: 'admin123' }

function App() {
  const [loggedIn, setLoggedIn] = useState(false)
  const navigate = useNavigate()

  const login = (username, password) => {
    const ok = username === DEMO_USER.username && password === DEMO_USER.password
    if (ok) setLoggedIn(true)
    return ok
  }

  const logout = () => {
    setLoggedIn(false)
    navigate('/login', { replace: true })
  }

  return (
    <Routes>
      <Route element={<Layout loggedIn={loggedIn} onLogout={logout} />}>
        <Route index element={<HomePage />} />
        <Route path="login" element={<LoginPage loggedIn={loggedIn} onLogin={login} />} />
        <Route element={<RequireAuth loggedIn={loggedIn} />}>
          <Route path="admin" element={<AdminDashboardPage />} />
          <Route path="admin/reports" element={<AdminReportsPage />} />
        </Route>
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  )
}

export default App
