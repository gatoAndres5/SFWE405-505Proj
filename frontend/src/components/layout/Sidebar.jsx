import { NavLink } from "react-router-dom";

const navItems = [
  { to: "/dashboard", label: "Dashboard" },
  { to: "/events", label: "Events" },
  { to: "/venues", label: "Venues" },
  { to: "/participants", label: "Participants" },
  { to: "/vendors", label: "Vendors" },
  { to: "/bookings", label: "Bookings" },
  { to: "/registrations", label: "Registrations" },
  { to: "/admin", label: "Admin Management" }
];

function getUserFromToken() {
  const token = localStorage.getItem("token");
  if (!token) return null;

  try {
    return JSON.parse(atob(token.split(".")[1]));
  } catch {
    return null;
  }
}

export default function Sidebar({ onLogout }) {
  const user = getUserFromToken();

  const username =
    user?.username ||
    user?.sub ||
    user?.email ||
    "User";

  const role =
    user?.role ||
    user?.authorities ||
    "";

  return (
    <aside className="sidebar">
      <div>
        <div className="sidebar-brand">
          <h2>Placeholder Name for Our System</h2>
          <p>Event Management</p>
        </div>

        <nav className="sidebar-nav">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                isActive ? "nav-button active" : "nav-button"
              }
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
      </div>

      <div className="sidebar-footer">
        <div className="sidebar-user">
          <span className="sidebar-user-label">Signed in as</span>
          <strong className="sidebar-user-name">{username}</strong>
          {role && <span className="sidebar-user-role">{role}</span>}
        </div>

        <button className="logout-button" onClick={onLogout}>
          Logout
        </button>
      </div>
    </aside>
  );
}