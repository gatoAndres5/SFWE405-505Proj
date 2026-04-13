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
    <div
      style={{
        marginBottom: "1rem",
        padding: "0.75rem",
        border: "1px solid rgba(255,255,255,0.15)",
        borderRadius: "8px",
      }}
    >
      <strong>{event.name ?? "Unnamed Event"}</strong>
      <div>
        {formatDateTime(event.startDateTime)} → {formatDateTime(event.endDateTime)}
      </div>
      <div>Status: {event.status ?? "N/A"}</div>
      <div>Venue: {getVenueSummary(event)}</div>
      <div style={{ marginTop: "0.35rem" }}>
        {event.description || "No description available."}
      </div>
    </div>
  );
}