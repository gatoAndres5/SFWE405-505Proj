import "./ScheduleItems.css";

const TYPE_OPTIONS = ["Workshop", "Keynote", "Panel", "Break", "Networking", "Other"];

export default function ScheduleItemForm({ form, onFieldChange, onSubmit, onCancel, isEditing }) {
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
            <label className="schedule-label">Event ID</label>
            <input
              className="schedule-input"
              type="number"
              placeholder="e.g. 1"
              value={form.eventId}
              onChange={(e) => onFieldChange("eventId", e.target.value)}
              required
            />
          </div>

          <div className="schedule-form-group">
            <label className="schedule-label">Venue ID</label>
            <input
              className="schedule-input"
              type="number"
              placeholder="e.g. 1"
              value={form.venueId}
              onChange={(e) => onFieldChange("venueId", e.target.value)}
              required
            />
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
