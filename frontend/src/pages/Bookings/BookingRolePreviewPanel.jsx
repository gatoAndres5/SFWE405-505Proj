import "./Bookings.css";

export default function BookingRolePreviewPanel({
  realIsAdmin,
  rolePreview,
  setRolePreview,
  participantPreviewId,
  setParticipantPreviewId,
  participants,
  getParticipantLabel,
  organizerPreviewId,
  setOrganizerPreviewId,
  organizers,
  getOrganizerLabel,
  staffPreviewId,
  setStaffPreviewId,
  staffUsers,
  getStaffLabel,
}) {
  if (!realIsAdmin) return null;

  return (
    <div className="book-form">
      <div className="book-form-grid">
        <div className="book-form-group">
          <label className="book-label" htmlFor="bookingRolePreview">
            Role Preview
          </label>
          <select
            className="book-select"
            id="bookingRolePreview"
            value={rolePreview}
            onChange={(e) => setRolePreview(e.target.value)}
          >
            <option value="">My Real Role (ADMIN)</option>
            <option value="ADMIN">ADMIN</option>
            <option value="ORGANIZER">ORGANIZER</option>
            <option value="STAFF">STAFF</option>
            <option value="PARTICIPANT">PARTICIPANT</option>
          </select>
        </div>

        {rolePreview === "ORGANIZER" && (
          <div className="book-form-group">
            <label className="book-label" htmlFor="bookingOrganizerPreview">
              Organizer Preview
            </label>
            <select
              className="book-select"
              id="bookingOrganizerPreview"
              value={organizerPreviewId}
              onChange={(e) => setOrganizerPreviewId(e.target.value)}
            >
              <option value="">Select organizer to preview</option>
              {organizers.map((organizer) => (
                <option key={organizer.id} value={organizer.id}>
                  {getOrganizerLabel(organizer)}
                </option>
              ))}
            </select>
          </div>
        )}

        {rolePreview === "STAFF" && (
          <div className="book-form-group">
            <label className="book-label" htmlFor="bookingStaffPreview">
              Staff Preview
            </label>
            <select
              className="book-select"
              id="bookingStaffPreview"
              value={staffPreviewId}
              onChange={(e) => setStaffPreviewId(e.target.value)}
            >
              <option value="">Select staff user to preview</option>
              {staffUsers.map((staff) => (
                <option key={staff.id} value={staff.id}>
                  {getStaffLabel(staff)}
                </option>
              ))}
            </select>
          </div>
        )}

        {rolePreview === "PARTICIPANT" && (
          <div className="book-form-group">
            <label className="book-label" htmlFor="bookingParticipantPreview">
              Participant Preview
            </label>
            <select
              className="book-select"
              id="bookingParticipantPreview"
              value={participantPreviewId}
              onChange={(e) => setParticipantPreviewId(e.target.value)}
            >
              <option value="">Select participant to preview</option>
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
        )}
      </div>
    </div>
  );
}