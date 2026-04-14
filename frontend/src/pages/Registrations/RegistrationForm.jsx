import EventSummaryCard from "./EventSummaryCard";
import ParticipantSummaryCard from "./ParticipantSummaryCard";

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
    <form onSubmit={handleCreateRegistration} style={{ marginBottom: "1.5rem" }}>
      <h3>Create Registration</h3>

      <div style={{ marginBottom: "0.75rem" }}>
        <label htmlFor="eventSelect">Event</label>
        <br />
        <select
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

      {selectedEvent && (
        <EventSummaryCard
          event={selectedEvent}
          formatDateTime={formatDateTime}
        />
      )}

      {canSelectParticipant ? (
        <>
          <div style={{ marginBottom: "0.75rem" }}>
            <label htmlFor="participantSelect">Participant</label>
            <br />
            <select
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

          {selectedParticipant && (
            <ParticipantSummaryCard participant={selectedParticipant} />
          )}
        </>
      ) : isParticipant ? (
        <>
          <div style={{ marginBottom: "0.75rem" }}>
            <label>Participant</label>
            <br />
            <input
              type="text"
              value={`Participant ID: ${effectiveParticipantId ?? "Unavailable"}`}
              readOnly
            />
          </div>

          {selectedParticipant && (
            <ParticipantSummaryCard participant={selectedParticipant} />
          )}
        </>
      ) : null}

      <div style={{ marginBottom: "0.75rem" }}>
        <label htmlFor="notes">Notes</label>
        <br />
        <textarea
          id="notes"
          value={notes}
          onChange={(e) => setNotes(e.target.value)}
          placeholder="Optional notes..."
          rows={3}
          style={{ width: "250px" }}
        />
      </div>

      <button type="submit" disabled={submitting}>
        {submitting
            ? "Submitting..."
            : isParticipant
            ? "Request Registration"
            : "Create Registration"}
        </button>
    </form>
  );
}