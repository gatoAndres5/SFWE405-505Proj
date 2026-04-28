export default function ParticipantDashboard({ user, dashboardData }) {
  const stats = [
    {
      label: "My Registrations",
      value: dashboardData?.stats?.myRegistrations ?? 0,
    },
    {
      label: "Approved Registrations",
      value: dashboardData?.stats?.approvedRegistrations ?? 0,
    },
    {
      label: "Pending Registrations",
      value: dashboardData?.stats?.pendingRegistrations ?? 0,
    },
    {
      label: "Upcoming Events",
      value: dashboardData?.stats?.upcomingEvents ?? 0,
    },
  ];

  const upcomingEvents = dashboardData?.upcomingEvents ?? [];

  return (
    <>
      <section className="dashboard-hero">
        <div>
          <h1>Dashboard</h1>
          <p>
            Welcome back, {user?.name}. Check your registration status, view upcoming
            events, and complete your participant profile.
          </p>
        </div>
        <div className="dashboard-hero-badge">Participant View</div>
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
              <p>Your registered and upcoming event activity.</p>
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
                      {event.venue?.name || event.venueName ? (
                        <p>{event.venue?.name || event.venueName}</p>
                      ) : (
                        <p className="muted">No venue</p>
                      )}
                    </div>

                    <span
                      className={`dashboard-status-badge ${(
                        event.registrationStatus || "pending"
                      ).toLowerCase()}`}
                    >
                      {event.registrationStatus || "PENDING"}
                    </span>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>

       <div className="dashboard-side-column">
        <div className="dashboard-panel">
          <div className="dashboard-panel-header">
            <h2>Registration Status</h2>
            <p>Your current registration updates.</p>
          </div>

          <div className="dashboard-alert-list">
            <div className="dashboard-alert-item">
              {dashboardData?.stats?.pendingRegistrations ?? 0} registrations are still pending.
            </div>
            <div className="dashboard-alert-item">
              {dashboardData?.stats?.approvedRegistrations ?? 0} registrations are approved.
            </div>
            <div className="dashboard-alert-item">
              {dashboardData?.stats?.upcomingEvents ?? 0} upcoming events on your schedule.
            </div>
          </div>
        </div>

        <div className="dashboard-panel">
          <div className="dashboard-panel-header">
            <h2>Complete Your Profile</h2>
            <p>Finish or update your participant account information.</p>
          </div>

          <div className="dashboard-alert-list">
            <div className="dashboard-alert-item">
              Review your participant details and make sure your profile information is up to date.
            </div>
            <div className="dashboard-alert-item">
              Visit the Participants page to finish or edit your registration account.
            </div>
          </div>
        </div>
      </div>
      </section>
    </>
  );
}