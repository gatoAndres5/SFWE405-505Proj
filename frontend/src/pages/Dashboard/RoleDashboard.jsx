import ParticipantDashboard from "./ParticipantDashboard";
import StaffDashboard from "./StaffDashboard";
import AdminDashboard from "./AdminDashboard";

export default function RoleDashboard({ user }) {
  const role = user?.role;

  if (role === "PARTICIPANT") {
    return <ParticipantDashboard user={user} />;
  }

  if (role === "STAFF" || role === "ORGANIZER") {
    return <StaffDashboard user={user} />;
  }

  if (role === "ADMIN") {
    return <AdminDashboard user={user} />;
  }

  return (
    <div className="dashboard-panel">
      <div className="dashboard-panel-header">
        <h2>Dashboard</h2>
        <p>No dashboard is available for this user role.</p>
      </div>
    </div>
  );
}