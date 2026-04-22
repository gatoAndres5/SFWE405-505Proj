export default function StaffDashboard({ user }) {
  const stats = [
    { label: "Assigned Events", value: 3 },
    { label: "Pending Registrations", value: 7 },
    { label: "Bookings", value: 5 },
    { label: "Schedule Items", value: 12 },
  ];

  const assignedEvents = [
    {
      id: 1,
      title: "Spring Conference",
      date: "Apr 25, 2026",
      time: "10:00 AM",
      venue: "Student Union",
      registrations: 24,
    },
    {
      id: 2,
      title: "Vendor Fair",
      date: "Apr 29, 2026",
      time: "9:00 AM",
      venue: "Main Hall",
      registrations: 18,
    },
    {
      id: 3,
      title: "Leadership Summit",
      date: "May 03, 2026",
      time: "11:00 AM",
      venue: "Innovation Center",
      registrations: 32,
    },
  ];

  const alerts = [
    "2 registrations are awaiting review for your assigned events.",
    "1 booking request still needs confirmation.",
    "An assigned event begins within the next 24 hours.",
  ];

  return (
    <>
      <section className="dashboard-hero">
        <div>
          <h1>Dashboard</h1>
          <p>
            Welcome back, {user?.name}. Monitor your assigned events, schedules,
            registrations, and venue locations.
          </p>
        </div>
        <div className="dashboard-hero-badge">Staff / Organizer View</div>
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
              <h2>Assigned Events</h2>
              <p>Overview of events connected to your role.</p>
            </div>

            <div className="dashboard-list">
              {assignedEvents.map((event) => (
                <div key={event.id} className="dashboard-list-item">
                  <div>
                    <h3>{event.title}</h3>
                    <p>{event.date} • {event.time}</p>
                    <p>{event.venue}</p>
                  </div>
                  <div className="dashboard-list-meta">
                    <strong>{event.registrations}</strong>
                    <span>Registrations</span>
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="dashboard-panel">
            <div className="dashboard-panel-header">
              <h2>Venue Map Preview</h2>
              <p>See where your assigned events are taking place.</p>
            </div>

            <div className="dashboard-map-placeholder">
              <div className="dashboard-map-box">
                <span>Map Preview</span>
                <p>Good place for venue pins later.</p>
              </div>
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