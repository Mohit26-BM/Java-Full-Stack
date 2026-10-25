import { NavLink, Navigate, Route, Routes } from 'react-router-dom'
import Register from './Register'
import Display from './Display'
import Login from './Login'

function App() {
  return (
    <>
      <nav className="navbar navbar-expand bg-body-tertiary border-bottom">
        <div className="container">
          <span className="navbar-brand">BootstrapDemo</span>
          <div className="navbar-nav">
            <NavLink className="nav-link" to="/register">Register</NavLink>
            <NavLink className="nav-link" to="/display">Display</NavLink>
            <NavLink className="nav-link" to="/login">Login</NavLink>
          </div>
        </div>
      </nav>

      <main className="container my-4" style={{ maxWidth: 640 }}>
        <Routes>
          <Route path="/" element={<Navigate to="/register" replace />} />
          <Route path="/register" element={<Register />} />
          <Route path="/display" element={<Display />} />
          <Route path="/login" element={<Login />} />
          <Route path="*" element={<Navigate to="/register" replace />} />
        </Routes>
      </main>
    </>
  )
}

export default App
