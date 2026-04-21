import "./Registrations.css";
export default function EventSummaryCard({ event, formatDateTime }) {
  function getVenueSummary(currentEvent) {
    if (!currentEvent?.venues || currentEvent.venues.length === 0) {
      return "No venue assigned";
    }

    const firstVenue = currentEvent.venues[0];
    return firstVenue?.name ?? "Venue assigned";
  }

  if (!event) return null;

  return (
    <div className="reg-summary-card">
      <h4>{event.name ?? "Unnamed Event"}</h4>

      <p>
        <span className="reg-muted">Time:</span>{" "}
        {formatDateTime(event.startDateTime)} →{" "}
        {formatDateTime(event.endDateTime)}
      </p>

      <p>
        <span className="reg-muted">Status:</span>{" "}
        <span className={`reg-badge ${event.status?.toLowerCase()}`}>
          {event.status ?? "N/A"}
        </span>
      </p>

      <p>
        <span className="reg-muted">Venue:</span>{" "}
        {getVenueSummary(event)}
      </p>

      <p className="reg-small reg-muted">
        {event.description || "No description available."}
      </p>
    </div>
  );
}