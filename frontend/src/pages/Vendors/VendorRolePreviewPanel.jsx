import "./Vendors.css";

export default function VendorRolePreviewPanel({
  realIsAdmin,
  rolePreview,
  setRolePreview,
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
    <div className="vnd-preview-row">
      <div className="vnd-preview-box">
        <label className="vnd-label" htmlFor="vndRolePreview">
          Role Preview
        </label>
        <select
          className="vnd-select"
          id="vndRolePreview"
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
        <div className="vnd-preview-box">
          <label className="vnd-label" htmlFor="vndOrganizerPreview">
            Organizer Preview
          </label>
          <select
            className="vnd-select"
            id="vndOrganizerPreview"
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
        <div className="vnd-preview-box">
          <label className="vnd-label" htmlFor="vndStaffPreview">
            Staff Preview
          </label>
          <select
            className="vnd-select"
            id="vndStaffPreview"
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
    </div>
  );
}
