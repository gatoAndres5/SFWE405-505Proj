export default function AdminDashboard({ user }) {
  const stats = [
    { label: "Total Events", value: 12 },
    { label: "Active Events", value: 5 },
    { label: "Participants", value: 148 },
    { label: "Pending Registrations", value: 9 },
    { label: "Active Bookings", value: 6 },
    { label: "Vendors", value: 18 },
  ];

  const upcomingEvents = [
    {
      id: 1,
      title: "Spring Conference",
      date: "Apr 25, 2026",
      time: "10:00 AM",
      venue: "Student Union",
      category: "Conference",
    },
    {
      id: 2,
      title: "Vendor Fair",
      date: "Apr 29, 2026",
      time: "9:00 AM",
      venue: "Main Hall",
      category: "Expo",
    },
    {
      id: 3,
      title: "Leadership Summit",
      date: "May 03, 2026",
      time: "11:00 AM",
      venue: "Innovation Center",
      category: "Summit",
    },
    {
      id: 4,
      title: "Community Meetup",
      date: "May 06, 2026",
      time: "5:30 PM",
      venue: "Downtown Center",
      category: "Meetup",
    },
  ];

  const alerts = [
    "3 registrations are still pending review.",
    "2 bookings are awaiting confirmation.",
    "1 event starts within the next 24 hours.",
  ];

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
              <p>System-wide view of what is happening next.</p>
            </div>

            <div className="dashboard-list">
              {upcomingEvents.map((event) => (
                <div key={event.id} className="dashboard-list-item">
                  <div>
                    <h3>{event.title}</h3>
                    <p>{event.date} • {event.time}</p>
                    <p>{event.venue}</p>
                  </div>
                  <div className="dashboard-list-meta">
                    <strong>{event.category}</strong>
                    <span>Event Type</span>
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="dashboard-panel">
            <div className="dashboard-panel-header">
              <h2>Venue Map Preview</h2>
              <p>Visual overview of where venues are located.</p>
            </div>

            <div className="dashboard-map-placeholder">
              <div className="dashboard-map-box">
                <span>Map Preview</span>
                <p>Later this can display venue markers using venue coordinates.</p>
              </div>
            </div>
          </div>
        </div>

        <div className="dashboard-side-column">
          <div className="dashboard-panel">
            <div className="dashboard-panel-header">
              <h2>Recent Alerts</h2>
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

          <div className="dashboard-panel">
            <div className="dashboard-panel-header">
              <h2>System Notes</h2>
              <p>Placeholder content for layout preview.</p>
            </div>

            <div className="dashboard-notes-box">
              <p>
                Use this area later for announcements, reminders, or recently
                updated records.
              </p>
              <p>
                You could also show pending approvals, recent changes, or system
                summaries here.
              </p>
            </div>
          </div>
        </div>
      </section>
    </>
  );
}