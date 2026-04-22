import RoleDashboard from "./RoleDashboard";
import "./Dashboard.css";

export default function DashboardPage() {
  // Replace this later with your real auth/user role source
  const user = {
    name: "Andres",
    role: "ADMIN", // "PARTICIPANT", "STAFF", "ORGANIZER", "ADMIN"
  };

  return (
    <div className="dashboard-page">
      <RoleDashboard user={user} />
    </div>
  );
}