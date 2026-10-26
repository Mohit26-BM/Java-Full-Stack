import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useBank } from '../state/BankContext.jsx'

function RequireAuth() {
  const { user } = useBank()
  const location = useLocation()
  if (!user) return <Navigate to="/login" replace state={{ from: location.pathname }} />
  return <Outlet />
}

export default RequireAuth
