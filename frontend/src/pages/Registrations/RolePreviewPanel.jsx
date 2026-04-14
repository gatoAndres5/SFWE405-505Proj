export default function RolePreviewPanel({
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
    <div style={{ marginBottom: "1.5rem" }}>
      <h3>Preview Page As</h3>

      <div style={{ marginBottom: "0.75rem" }}>
        <label htmlFor="rolePreview">Role Preview</label>
        <br />
        <select
          id="rolePreview"
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
        <div style={{ marginBottom: "0.75rem" }}>
          <label htmlFor="organizerPreview">Organizer Preview</label>
          <br />
          <select
            id="organizerPreview"
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
        <div style={{ marginBottom: "0.75rem" }}>
          <label htmlFor="staffPreview">Staff Preview</label>
          <br />
          <select
            id="staffPreview"
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
        <div style={{ marginBottom: "0.75rem" }}>
          <label htmlFor="participantPreview">Participant Preview</label>
          <br />
          <select
            id="participantPreview"
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
  );
}