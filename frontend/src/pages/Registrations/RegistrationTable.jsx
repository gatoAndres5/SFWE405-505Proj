export default function RegistrationTable({
  registrations,
  canUpdateStatus,
  canCancel,
  canCheckInOut,
  canDelete,
  selectedStatus,
  handleStatusChange,
  handleUpdateStatus,
  handleCancelRegistration,
  handleCheckIn,
  handleCheckOut,
  handleDeleteRegistration,
  getEventName,
  getParticipantName,
  formatDateTime,
  statusOptions,
}) {
  return (
    <div style={{ overflowX: "auto" }}>
      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead>
          <tr>
            <th style={{ textAlign: "left", padding: "0.5rem" }}>ID</th>
            <th style={{ textAlign: "left", padding: "0.5rem" }}>Event ID</th>
            <th style={{ textAlign: "left", padding: "0.5rem" }}>Event Name</th>
            <th style={{ textAlign: "left", padding: "0.5rem" }}>Participant ID</th>
            <th style={{ textAlign: "left", padding: "0.5rem" }}>Participant Name</th>
            <th style={{ textAlign: "left", padding: "0.5rem" }}>Status</th>
            <th style={{ textAlign: "left", padding: "0.5rem" }}>Check-In</th>
            <th style={{ textAlign: "left", padding: "0.5rem" }}>Registered At</th>
            <th style={{ textAlign: "left", padding: "0.5rem" }}>Notes</th>
            {canUpdateStatus && (
              <th style={{ textAlign: "left", padding: "0.5rem" }}>Update Status</th>
            )}
            {(canCancel || canCheckInOut || canDelete) && (
              <th style={{ textAlign: "left", padding: "0.5rem" }}>Actions</th>
            )}
          </tr>
        </thead>
        <tbody>
          {registrations.map((registration) => (
            <tr key={registration.id}>
              <td style={{ padding: "0.5rem" }}>{registration.id ?? "N/A"}</td>
              <td style={{ padding: "0.5rem" }}>
                {registration.event?.id ?? "N/A"}
              </td>
              <td style={{ padding: "0.5rem" }}>
                {getEventName(registration.event)}
              </td>
              <td style={{ padding: "0.5rem" }}>
                {registration.participant?.participantId ?? "N/A"}
              </td>
              <td style={{ padding: "0.5rem" }}>
                {getParticipantName(registration.participant)}
              </td>
              <td style={{ padding: "0.5rem" }}>
                {registration.registrationStatus ?? "N/A"}
              </td>
              <td style={{ padding: "0.5rem" }}>
                {registration.checkInStatus ? "Checked In" : "Not Checked In"}
              </td>
              <td style={{ padding: "0.5rem" }}>
                {registration.registeredAt
                  ? formatDateTime(registration.registeredAt)
                  : "N/A"}
              </td>
              <td style={{ padding: "0.5rem" }}>
                {registration.notes || "—"}
              </td>

              {canUpdateStatus && (
                <td style={{ padding: "0.5rem" }}>
                  <select
                    value={selectedStatus[registration.id] || ""}
                    onChange={(e) =>
                      handleStatusChange(registration.id, e.target.value)
                    }
                  >
                    <option value="">Select status</option>
                    {statusOptions.map((status) => (
                      <option key={status} value={status}>
                        {status}
                      </option>
                    ))}
                  </select>
                  <button
                    type="button"
                    style={{ marginLeft: "0.5rem" }}
                    onClick={() => handleUpdateStatus(registration.id)}
                  >
                    Update
                  </button>
                </td>
              )}

              {(canCancel || canCheckInOut || canDelete) && (
                <td style={{ padding: "0.5rem" }}>
                  <div style={{ display: "flex", gap: "0.5rem", flexWrap: "wrap" }}>
                    {canCancel && (
                      <button
                        type="button"
                        onClick={() => handleCancelRegistration(registration.id)}
                      >
                        Cancel
                      </button>
                    )}

                    {canCheckInOut && (
                      <>
                        <button
                          type="button"
                          onClick={() => handleCheckIn(registration.id)}
                        >
                          Check In
                        </button>
                        <button
                          type="button"
                          onClick={() => handleCheckOut(registration.id)}
                        >
                          Check Out
                        </button>
                      </>
                    )}

                    {canDelete && (
                      <button
                        type="button"
                        onClick={() => handleDeleteRegistration(registration.id)}
                      >
                        Delete
                      </button>
                    )}
                  </div>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}