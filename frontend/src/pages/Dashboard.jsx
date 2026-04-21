export default function Dashboard() {
  return (
    <div className="content-card">
      <h2>Dashboard</h2>
      <p>Welcome to the Event Planning and Coordination platform.</p>

      <div className="stats-grid">
        <div className="stat-box">
          <h3>Events</h3>
          <p>Manage event records and details.</p>
        </div>
        <div className="stat-box">
          <h3>Venues</h3>
          <p>Track venue details and availability.</p>
        </div>
        <div className="stat-box">
          <h3>Participants</h3>
          <p>View and maintain participant information.</p>
        </div>
        <div className="stat-box">
          <h3>Bookings</h3>
          <p>Monitor vendor and service bookings.</p>
        </div>
      </div>
    </div>
  );
}