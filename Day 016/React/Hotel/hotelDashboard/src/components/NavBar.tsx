import { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../auth-context";

const links = [
  { to: "/", label: "View Hotels" },
  { to: "/add", label: "Add Hotel" },
  { to: "/update", label: "Update Hotel" },
  { to: "/delete", label: "Delete Hotel" },
  { to: "/search", label: "Search Hotels" },
];

function NavBar() {
  const { token, logout } = useAuth();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  const handleLogout = () => {
    setMenuOpen(false);
    logout();
    navigate("/login");
  };

  return (
    <nav className="navbar navbar-expand-md navbar-dark navbar-themed mb-4">
      <div className="container">
        <span className="navbar-brand fw-semibold">Hotel Dashboard</span>

        {token && (
          <>
            <button
              className="navbar-toggler"
              type="button"
              aria-label="Toggle navigation"
              aria-expanded={menuOpen}
              onClick={() => setMenuOpen((open) => !open)}
            >
              <span className="navbar-toggler-icon" />
            </button>

            <div className={`collapse navbar-collapse${menuOpen ? " show" : ""}`}>
              <ul className="navbar-nav ms-auto align-items-md-center">
                {links.map((link) => (
                  <li className="nav-item" key={link.to}>
                    <NavLink
                      to={link.to}
                      end={link.to === "/"}
                      className={({ isActive }) => `nav-link${isActive ? " active" : ""}`}
                      onClick={() => setMenuOpen(false)}
                    >
                      {link.label}
                    </NavLink>
                  </li>
                ))}
                <li className="nav-item ms-md-2">
                  <button className="btn btn-sm btn-outline-light" onClick={handleLogout}>
                    Logout
                  </button>
                </li>
              </ul>
            </div>
          </>
        )}
      </div>
    </nav>
  );
}

export default NavBar;
