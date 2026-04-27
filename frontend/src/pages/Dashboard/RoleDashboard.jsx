import ParticipantDashboard from "./ParticipantDashboard";
import StaffDashboard from "./StaffDashboard";
import AdminDashboard from "./AdminDashboard";

export default function RoleDashboard({ user, dashboardData }) {
  const role = user?.role;

  if (role === "PARTICIPANT") {
    return <ParticipantDashboard user={user} dashboardData={dashboardData} />;
  }

  if (role === "STAFF" || role === "ORGANIZER") {
    return <StaffDashboard user={user} dashboardData={dashboardData} />;
  }

  if (role === "ADMIN") {
    return <AdminDashboard user={user} dashboardData={dashboardData} />;
  }

  return <p>No dashboard available.</p>;
}