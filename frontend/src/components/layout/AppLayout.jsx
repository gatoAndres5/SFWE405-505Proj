import Sidebar from "./Sidebar";
import { useLocation } from "react-router-dom";

export default function AppLayout({ children, onLogout }) {
  const location = useLocation();

  function getPageTitle(pathname) {
    switch (pathname) {
      case "/dashboard":
        return "Dashboard";
      case "/events":
        return "Events";
      case "/venues":
        return "Venues";
      case "/participants":
        return "Participants";
      case "/vendors":
        return "Vendors";
      case "/bookings":
        return "Bookings";
      case "/registrations":
        return "Registrations";
      case "/admin":
        return "Admin Management";
      default:
        return "Dashboard";
    }
  }

  const pageTitle = getPageTitle(location.pathname);

  return (
    <div className="dashboard-layout">
      <Sidebar onLogout={onLogout} />

      <main className="main-content">
        <header className="topbar">
          <div>
            <h1>{pageTitle}</h1>
            <p>Manage your application data from this section.</p>
          </div>
        </header>

        <section className="page-section">{children}</section>
      </main>
    </div>
  );
}