export default function AdminDashboard({ user, dashboardData }) {
  const stats = [
    { label: "Total Events", value: dashboardData?.stats?.totalEvents ?? 0 },
    { label: "Active Events", value: dashboardData?.stats?.activeEvents ?? 0 },
    {
      label: "Participants",
      value: dashboardData?.stats?.totalParticipants ?? 0,
    },
    {
      label: "Pending Registrations",
      value: dashboardData?.stats?.pendingRegistrations ?? 0,
    },
    {
      label: "Active Bookings",
      value: dashboardData?.stats?.activeBookings ?? 0,
    },
  ];

  const upcomingEvents = (dashboardData?.upcomingEvents ?? []).slice(0, 3);

  return (
    <>
      <section className="dashboard-hero">
        <div>
          <h1>Dashboard</h1>
          <p>
            Welcome back, {user?.name}. Monitor events, registrations, bookings,
            and venue activity across the platform.
          </p>
        </div>
        <div className="dashboard-hero-badge">Admin View</div>
      </section>

      <section className="dashboard-stats-grid compact">
        {stats.map((stat) => (
          <div key={stat.label} className="dashboard-stat-card compact">
            <p className="dashboard-stat-label">{stat.label}</p>
            <h2 className="dashboard-stat-value">{stat.value}</h2>
          </div>
        ))}
      </section>

      <section className="dashboard-main-grid">
        <div className="dashboard-content-column">
          <div className="dashboard-panel">
            <div className="dashboard-panel-header">
              <h2>Upcoming Events</h2>
              <p>System-wide view of what is happening next.</p>
            </div>

            <div className="dashboard-list">
              {upcomingEvents.length === 0 ? (
                <div className="dashboard-empty-state">
                  <p>No upcoming events found.</p>
                </div>
              ) : (
                upcomingEvents.map((event) => (
                  <div key={event.id} className="dashboard-list-item">
                    <div>
                      <h3>{event.name}</h3>
                      <p>{new Date(event.startDateTime).toLocaleString()}</p>
                      <p>
                        {event.venue?.name ||
                          event.venueName ||
                          "No venue assigned"}
                      </p>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>

        <div className="dashboard-side-column">
          <div className="dashboard-panel">
            <div className="dashboard-panel-header">
              <h2>Recent Alerts</h2>
              <p>Quick system summary.</p>
            </div>

            <div className="dashboard-alert-list">
              <div className="dashboard-alert-item">
                {dashboardData?.stats?.pendingRegistrations ?? 0} pending
                registrations.
              </div>
              <div className="dashboard-alert-item">
                {dashboardData?.stats?.activeBookings ?? 0} active bookings.
              </div>
              <div className="dashboard-alert-item">
                {dashboardData?.stats?.activeEvents ?? 0} active events right
                now.
              </div>
            </div>
          </div>

          <div className="dashboard-panel">
            <div className="dashboard-panel-header">
              <h2>Quick Summary</h2>
              <p>Current platform snapshot.</p>
            </div>

            <div className="dashboard-alert-list">
              <div className="dashboard-alert-item">
                {dashboardData?.stats?.totalEvents ?? 0} total events in the
                system.
              </div>
              <div className="dashboard-alert-item">
                {dashboardData?.stats?.totalParticipants ?? 0} registered
                participants.
              </div>
              <div className="dashboard-alert-item">
                {dashboardData?.upcomingEvents?.length ?? 0} upcoming events
                scheduled.
              </div>
            </div>
          </div>
        </div>
      </section>
    </>
  );
}