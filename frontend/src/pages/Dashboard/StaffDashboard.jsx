export default function StaffDashboard({ user, dashboardData }) {
  const isOrganizer = user?.role === "ORGANIZER";

  const stats = [
    {
      label: "Assigned Events",
      value: dashboardData?.stats?.assignedEvents ?? 0,
    },
    {
      label: "Pending Registrations",
      value: dashboardData?.stats?.pendingRegistrations ?? 0,
    },
    {
      label: "Bookings",
      value: dashboardData?.stats?.activeBookings ?? 0,
    },
    {
      label: "Schedule Items",
      value: dashboardData?.stats?.scheduleItems ?? 0,
    },
  ];

  const assignedEvents = (dashboardData?.assignedEvents ?? []).slice(0, 3);
  

  return (
    <>
      <section className="dashboard-hero">
        <div>
          <h1>Dashboard</h1>
          <p>
            Welcome back, {user?.name}.{" "}
            {isOrganizer
              ? "Manage your assigned events and oversee registrations and bookings."
              : "Monitor your assigned events and support event operations."}
          </p>
        </div>
        <div className="dashboard-hero-badge">
          {isOrganizer ? "Organizer View" : "Staff View"}
        </div>
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
              <h2>Assigned Events</h2>
              <p>Events you are responsible for.</p>
            </div>

            <div className="dashboard-list">
              {assignedEvents.length === 0 ? (
                <div className="dashboard-empty-state">
                  <p>No assigned events found.</p>
                </div>
              ) : (
                assignedEvents.map((event) => (
                  <div key={event.id} className="dashboard-list-item">
                    <div>
                      <h3>{event.name}</h3>
                      <p>
                        {new Date(event.startDateTime).toLocaleString()}
                      </p>
                      {event.venue?.name || event.venueName ? (
                        <p>{event.venue?.name || event.venueName}</p>
                      ) : (
                        <p className="muted">No venue</p>
                      )}
                    </div>

                    <div className="dashboard-list-meta">
                      <strong>
                        {event.registrationCount ?? 0}
                      </strong>
                      <span>Registrations</span>
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
              <h2>Alerts</h2>
              <p>Items that may need attention.</p>
            </div>

            <div className="dashboard-alert-list">
              <div className="dashboard-alert-item">
                {dashboardData?.stats?.pendingRegistrations ?? 0} registrations
                awaiting review.
              </div>
              <div className="dashboard-alert-item">
                {dashboardData?.stats?.activeBookings ?? 0} active bookings.
              </div>
              <div className="dashboard-alert-item">
                {dashboardData?.stats?.assignedEvents ?? 0} assigned events.
              </div>
            </div>
          </div>

          <div className="dashboard-panel">
            <div className="dashboard-panel-header">
              <h2>Quick Summary</h2>
              <p>Your current workload snapshot.</p>
            </div>

            <div className="dashboard-alert-list">
              <div className="dashboard-alert-item">
                {dashboardData?.stats?.assignedEvents ?? 0} total assigned
                events.
              </div>
              <div className="dashboard-alert-item">
                {dashboardData?.stats?.pendingRegistrations ?? 0} pending
                registrations.
              </div>
              <div className="dashboard-alert-item">
                {assignedEvents.length} upcoming events.
              </div>
            </div>
          </div>
        </div>
      </section>
    </>
  );
}