import { NavLink } from "react-router-dom";

const navItems = [
  { to: "/dashboard", label: "Dashboard" },
  { to: "/events", label: "Events" },
  { to: "/venues", label: "Venues" },
  { to: "/participants", label: "Participants" },
  { to: "/vendors", label: "Vendors" },
  { to: "/bookings", label: "Bookings" },
  { to: "/registrations", label: "Registrations" },
  { to: "/admin", label: "Admin Management"}
];

export default function Sidebar({ onLogout }) {
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

      <button className="logout-button" onClick={onLogout}>
        Logout
      </button>
    </aside>
  );
}