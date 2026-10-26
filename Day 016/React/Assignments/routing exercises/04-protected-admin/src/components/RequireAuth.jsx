import { Navigate, Outlet, useLocation } from 'react-router-dom'

function RequireAuth({ loggedIn }) {
  const location = useLocation()
  if (!loggedIn) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />
  }
  return <Outlet />
}

export default RequireAuth
