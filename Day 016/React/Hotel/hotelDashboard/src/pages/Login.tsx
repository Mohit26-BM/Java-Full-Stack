import { useState } from "react";
import type { FormEvent } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../auth-context";
import ErrorAlert from "../components/ErrorAlert";
import { LOGIN_URL, getErrorMessage } from "../api";

function Login() {
  const { token, login } = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  // Already logged in: nothing to do here.
  if (token) {
    return <Navigate to="/" replace />;
  }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setError("");

    try {
      const response = await axios.post<{ token: string }>(LOGIN_URL, {
        username: username.trim(),
        password,
      });
      login(response.data.token);
      navigate("/", { replace: true });
    } catch (err) {
      setError(getErrorMessage(err, "Cannot reach the server. Please try again."));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="row justify-content-center py-md-4">
      <div className="login-backdrop" aria-hidden="true" />
      <div className="col-12 col-md-8 col-lg-5">
        <h2 className="login-title mb-3 text-center">Admin Login</h2>
        <ErrorAlert message={error} />

        <form onSubmit={handleSubmit} className="card login-card shadow">
          <div className="card-body">
            <div className="form-floating mb-3">
              <input
                id="login-username"
                className="form-control"
                placeholder="Username"
                autoComplete="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
              <label htmlFor="login-username">Username</label>
            </div>

            <div className="form-floating mb-4">
              <input
                id="login-password"
                type="password"
                className="form-control"
                placeholder="Password"
                autoComplete="current-password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
              <label htmlFor="login-password">Password</label>
            </div>

            <button type="submit" className="btn btn-main w-100" disabled={submitting}>
              {submitting ? "Signing in..." : "Login"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default Login;
