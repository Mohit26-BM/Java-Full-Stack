import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../auth-context";

// Renders the nested routes only when logged in; otherwise sends the user to /login.
function ProtectedRoute() {
  const { token } = useAuth();

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}

export default ProtectedRoute;
