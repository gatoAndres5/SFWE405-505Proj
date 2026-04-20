import "./ScheduleItems.css";

const TYPE_OPTIONS = ["Workshop", "Keynote", "Panel", "Break", "Networking", "Other"];

function formatDateTime(dt) {
  if (!dt) return "—";
  return new Date(dt).toLocaleString();
}

export default function ScheduleItemForm({ form, onFieldChange, onSubmit, onCancel, isEditing, events = [], venues = [] }) {
  const selectedEvent = events.find((e) => String(e.id) === String(form.eventId));
  const selectedVenue = venues.find((v) => String(v.id) === String(form.venueId));

  return (
    <section className="schedule-section">
      <h2 className="schedule-section-title">
        {isEditing ? "Edit Schedule Item" : "New Schedule Item"}
      </h2>
      <p className="schedule-section-subtitle">
        {isEditing
          ? "Update the details below and save."
          : "Fill in the details to add a new item to the schedule."}
      </p>

      <form className="schedule-form" onSubmit={onSubmit}>
        <div className="schedule-form-grid">

          <div className="schedule-form-group">
            <label className="schedule-label">Event</label>
            <select
              className="schedule-select"
              value={form.eventId}
              onChange={(e) => onFieldChange("eventId", e.target.value)}
              required
            >
              <option value="">Select an event...</option>
              {events.map((event) => (
                <option key={event.id} value={event.id}>
                  {event.id} - {event.name}
                </option>
              ))}
            </select>
            {selectedEvent && (
              <div className="schedule-preview">
                <strong>{selectedEvent.name}</strong>
                <span>Time: {formatDateTime(selectedEvent.startDateTime)} → {formatDateTime(selectedEvent.endDateTime)}</span>
                <span>Status: <strong>{selectedEvent.status}</strong></span>
                {selectedEvent.description && <span>{selectedEvent.description}</span>}
              </div>
            )}
          </div>

          <div className="schedule-form-group">
            <label className="schedule-label">Venue</label>
            <select
              className="schedule-select"
              value={form.venueId}
              onChange={(e) => onFieldChange("venueId", e.target.value)}
              required
            >
              <option value="">Select a venue...</option>
              {venues.map((venue) => (
                <option key={venue.id} value={venue.id}>
                  {venue.id} - {venue.name}
                </option>
              ))}
            </select>
            {selectedVenue && (
              <div className="schedule-preview">
                <strong>{selectedVenue.name}</strong>
                {selectedVenue.address && (
                  <span>{selectedVenue.address.street}, {selectedVenue.address.city}, {selectedVenue.address.state}</span>
                )}
                <span>Capacity: {selectedVenue.capacity}</span>
                <span>Contact: {selectedVenue.contactName} — {selectedVenue.contactEmail}</span>
              </div>
            )}
          </div>

          <div className="schedule-form-group">
            <label className="schedule-label">Type</label>
            <select
              className="schedule-select"
              value={form.type}
              onChange={(e) => onFieldChange("type", e.target.value)}
              required
            >
              <option value="">Select a type...</option>
              {TYPE_OPTIONS.map((t) => (
                <option key={t} value={t}>
                  {t}
                </option>
              ))}
            </select>
          </div>

          <div className="schedule-form-group full-width">
            <label className="schedule-label">Title</label>
            <input
              className="schedule-input"
              type="text"
              placeholder="e.g. Opening Keynote"
              value={form.title}
              onChange={(e) => onFieldChange("title", e.target.value)}
              required
            />
          </div>

          <div className="schedule-form-group full-width">
            <label className="schedule-label">Description</label>
            <textarea
              className="schedule-textarea"
              placeholder="Brief description of this schedule item..."
              value={form.description}
              onChange={(e) => onFieldChange("description", e.target.value)}
              required
            />
          </div>

          <div className="schedule-form-group">
            <label className="schedule-label">Start Date & Time</label>
            <input
              className="schedule-input"
              type="datetime-local"
              value={form.startDateTime}
              onChange={(e) => onFieldChange("startDateTime", e.target.value)}
              required
            />
          </div>

          <div className="schedule-form-group">
            <label className="schedule-label">End Date & Time</label>
            <input
              className="schedule-input"
              type="datetime-local"
              value={form.endDateTime}
              onChange={(e) => onFieldChange("endDateTime", e.target.value)}
              required
            />
          </div>

        </div>

        <div className="schedule-actions-row">
          <button type="submit" className="schedule-btn schedule-btn-primary">
            {isEditing ? "Save Changes" : "Create Schedule Item"}
          </button>
          <button
            type="button"
            className="schedule-btn schedule-btn-secondary"
            onClick={onCancel}
          >
            Cancel
          </button>
        </div>
      </form>
    </section>
  );
}
