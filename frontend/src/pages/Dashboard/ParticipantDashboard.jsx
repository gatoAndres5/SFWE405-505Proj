export default function ParticipantDashboard({ user }) {
  const stats = [
    { label: "My Registrations", value: 4 },
    { label: "Approved", value: 2 },
    { label: "Pending", value: 2 },
    { label: "Upcoming Events", value: 3 },
  ];

  const upcomingEvents = [
    {
      id: 1,
      title: "Spring Conference",
      date: "Apr 25, 2026",
      time: "10:00 AM",
      venue: "Student Union",
      status: "Approved",
    },
    {
      id: 2,
      title: "Networking Night",
      date: "Apr 28, 2026",
      time: "6:00 PM",
      venue: "Main Hall",
      status: "Pending",
    },
    {
      id: 3,
      title: "Tech Expo",
      date: "May 02, 2026",
      time: "1:00 PM",
      venue: "Innovation Center",
      status: "Approved",
    },
  ];

  const alerts = [
    "Your registration for Networking Night is still pending.",
    "You have 1 event starting within the next 3 days.",
    "Registration is open for 2 active events.",
  ];

  return (
    <>
      <section className="dashboard-hero">
        <div>
          <h1>Dashboard</h1>
          <p>
            Welcome back, {user?.name}. View your registrations, upcoming
            events, and venue information.
          </p>
        </div>
        <div className="dashboard-hero-badge">Participant View</div>
      </section>

      <section className="dashboard-stats-grid">
        {stats.map((stat) => (
          <div key={stat.label} className="dashboard-stat-card">
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
              {upcomingEvents.map((event) => (
                <div key={event.id} className="dashboard-list-item">
                  <div>
                    <h3>{event.title}</h3>
                    <p>{event.date} • {event.time}</p>
                    <p>{event.venue}</p>
                  </div>
                  <span className={`dashboard-status-badge ${event.status.toLowerCase()}`}>
                    {event.status}
                  </span>
                </div>
              ))}
            </div>
          </div>

          <div className="dashboard-panel">
            <div className="dashboard-panel-header">
              <h2>Venue Map Preview</h2>
              <p>Preview where your upcoming events are located.</p>
            </div>

            <div className="dashboard-map-placeholder">
              <div className="dashboard-map-box">
                <span>Map Preview</span>
                <p>Replace this later with an actual map component.</p>
              </div>
            </div>
          </div>
        </div>

        <div className="dashboard-side-column">
          <div className="dashboard-panel">
            <div className="dashboard-panel-header">
              <h2>Notifications</h2>
              <p>Important updates related to your account.</p>
            </div>

            <div className="dashboard-alert-list">
              {alerts.map((alert, index) => (
                <div key={index} className="dashboard-alert-item">
                  {alert}
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>
    </>
  );
}