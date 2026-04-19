import "./Registrations.css";
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
  function getStatusBadgeClass(status) {
    switch (status) {
      case "PENDING":
        return "pending";
      case "INVITED":
        return "pending";
      case "CONFIRMED":
        return "confirmed";
      case "WAITLISTED":
        return "warning";
      case "CANCELLED":
        return "cancelled";
      default:
        return "";
    }
  }

  return (
    <div className="reg-table-wrapper">
      <table className="reg-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Event ID</th>
            <th>Event Name</th>
            <th>Participant ID</th>
            <th>Participant Name</th>
            <th>Status</th>
            <th>Check-In</th>
            <th>Registered At</th>
            <th>Notes</th>
            {canUpdateStatus && <th>Update Status</th>}
            {(canCancel || canCheckInOut || canDelete) && <th>Actions</th>}
          </tr>
        </thead>

        <tbody>
          {registrations.map((registration) => (
            <tr key={registration.id}>
              <td>{registration.id ?? "N/A"}</td>

              <td>{registration.event?.id ?? "N/A"}</td>

              <td>{getEventName(registration.event)}</td>

              <td>{registration.participant?.participantId ?? "N/A"}</td>

              <td>{getParticipantName(registration.participant)}</td>

              <td>
                <span
                  className={`reg-badge ${getStatusBadgeClass(
                    registration.registrationStatus
                  )}`}
                >
                  {registration.registrationStatus ?? "N/A"}
                </span>
              </td>

              <td>
                <span
                  className={`reg-checkin ${
                    registration.checkInStatus
                      ? "checked-in"
                      : "not-checked-in"
                  }`}
                >
                  {registration.checkInStatus ? "Checked In" : "Not Checked In"}
                </span>
              </td>

              <td>
                {registration.registeredAt
                  ? formatDateTime(registration.registeredAt)
                  : "N/A"}
              </td>

              <td>{registration.notes || "—"}</td>

              {canUpdateStatus && (
                <td>
                  <div className="reg-inline-update">
                    <select
                      className="reg-select"
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
                      className="reg-btn reg-btn-secondary"
                      type="button"
                      onClick={() => handleUpdateStatus(registration.id)}
                    >
                      Update
                    </button>
                  </div>
                </td>
              )}

              {(canCancel || canCheckInOut || canDelete) && (
                <td className="reg-actions-cell">
                  <div className="reg-actions-stack">
                    {canCancel && (
                      <button
                        className="reg-btn reg-btn-warning"
                        type="button"
                        onClick={() => handleCancelRegistration(registration.id)}
                      >
                        Cancel
                      </button>
                    )}

                    {canCheckInOut && (
                      <>
                        <button
                          className="reg-btn reg-btn-success"
                          type="button"
                          onClick={() => handleCheckIn(registration.id)}
                        >
                          Check In
                        </button>

                        <button
                          className="reg-btn reg-btn-outline"
                          type="button"
                          onClick={() => handleCheckOut(registration.id)}
                        >
                          Check Out
                        </button>
                      </>
                    )}

                    {canDelete && (
                      <button
                        className="reg-btn reg-btn-danger"
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