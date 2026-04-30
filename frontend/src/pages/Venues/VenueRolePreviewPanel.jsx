export default function VenueRolePreviewPanel({ rolePreview, participantPreviewId, organizerPreviewId, staffPreviewId, events }) {
  return (
    <div className="role-preview-panel">
      <h3>Role Preview: {rolePreview}</h3>
      
      {rolePreview && (
        <div className="role-info">
          <p><strong>Role:</strong> {rolePreview}</p>
          <p><strong>User ID:</strong> {participantPreviewId || organizerPreviewId || staffPreviewId || 'N/A'}</p>
        </div>
      )}

      {events.length > 0 && (
        <div className="associated-events">
          <h4>Associated Events ({events.length})</h4>
          <div className="events-list">
            {events.slice(0, 3).map(event => (
              <div key={event.id} className="event-item">
                <p><strong>{event.name}</strong></p>
                <p>{event.startDateTime} - {event.endDateTime}</p>
                <p>Status: {event.status}</p>
              </div>
            ))}
            {events.length > 3 && (
              <p className="more-events">... and {events.length - 3} more events</p>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
