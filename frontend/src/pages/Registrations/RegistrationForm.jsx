import EventSummaryCard from "./EventSummaryCard";
import ParticipantSummaryCard from "./ParticipantSummaryCard";
import "./Registrations.css";

export default function RegistrationForm({
  eventId,
  setEventId,
  participantId,
  setParticipantId,
  notes,
  setNotes,
  submitting,
  handleCreateRegistration,
  events,
  participants,
  selectedEvent,
  selectedParticipant,
  getEventLabel,
  getParticipantLabel,
  formatDateTime,
  effectiveParticipantId,
  canSelectParticipant,
  isParticipant,
}) {
  return (
    <form className="reg-form" onSubmit={handleCreateRegistration}>
      <div className="reg-form-grid">
        <div className="reg-form-group">
          <label className="reg-label" htmlFor="eventSelect">
            Event
          </label>
          <select
            className="reg-select"
            id="eventSelect"
            value={eventId}
            onChange={(e) => setEventId(e.target.value)}
            required
          >
            <option value="">Select an event</option>
            {events.map((event) => (
              <option key={event.id} value={event.id}>
                {getEventLabel(event)}
              </option>
            ))}
          </select>
        </div>

        {canSelectParticipant ? (
          <div className="reg-form-group">
            <label className="reg-label" htmlFor="participantSelect">
              Participant
            </label>
            <select
              className="reg-select"
              id="participantSelect"
              value={participantId}
              onChange={(e) => setParticipantId(e.target.value)}
              required
            >
              <option value="">Select a participant</option>
              {participants.map((participant) => (
                <option
                  key={participant.participantId}
                  value={participant.participantId}
                >
                  {getParticipantLabel(participant)}
                </option>
              ))}
            </select>
          </div>
        ) : isParticipant ? (
          <div className="reg-form-group">
            <label className="reg-label">Participant</label>
            <input
              className="reg-input"
              type="text"
              value={`Participant ID: ${effectiveParticipantId ?? "Unavailable"}`}
              readOnly
            />
          </div>
        ) : null}

        <div className="reg-form-group full-width">
          <label className="reg-label" htmlFor="notes">
            Notes
          </label>
          <textarea
            className="reg-textarea"
            id="notes"
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            placeholder="Optional notes..."
            rows={3}
          />
        </div>
      </div>

      {selectedEvent && (
        <div className="reg-summary-grid">
          <EventSummaryCard
            event={selectedEvent}
            formatDateTime={formatDateTime}
          />
        </div>
      )}

      {selectedParticipant && (
        <div className="reg-summary-grid">
          <ParticipantSummaryCard participant={selectedParticipant} />
        </div>
      )}

      <div className="reg-actions-row">
        <button
          className="reg-btn reg-btn-primary"
          type="submit"
          disabled={submitting}
        >
          {submitting
            ? "Submitting..."
            : isParticipant
            ? "Request Registration"
            : "Create Registration"}
        </button>
      </div>
    </form>
  );
}