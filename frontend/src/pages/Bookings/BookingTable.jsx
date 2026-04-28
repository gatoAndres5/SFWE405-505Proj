import "./Bookings.css";

export default function BookingTable({
  bookings,
  canManage,
  handleConfirmBooking,
  handleCancelBooking,
  handleCompleteBooking,
  formatDateTime,
}) {
  function getStatusBadgeClass(status) {
    switch (status) {
      case "REQUESTED":
        return "pending";
      case "CONFIRMED":
        return "confirmed";
      case "COMPLETED":
        return "completed";
      case "CANCELLED":
        return "cancelled";
      default:
        return "";
    }
  }

  function getEventName(event) {
    return event?.name ?? event?.title ?? "Unnamed Event";
  }

  function getVendorName(vendor) {
    return vendor?.name ?? vendor?.companyName ?? "Unnamed Vendor";
  }

  return (
    <div className="book-table-wrapper">
      <table className="book-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Event ID</th>
            <th>Event Name</th>
            <th>Vendor ID</th>
            <th>Vendor Name</th>
            <th>Description</th>
            <th>Status</th>
            <th>Start</th>
            <th>End</th>
            {canManage && <th>Actions</th>}
          </tr>
        </thead>

        <tbody>
          {bookings.map((booking) => (
            <tr key={booking.bookingId}>
              <td>{booking.bookingId ?? "N/A"}</td>
              <td>{booking.event?.id ?? "N/A"}</td>
              <td>{getEventName(booking.event)}</td>
              <td>{booking.vendor?.vendorId ?? booking.vendor?.id ?? "N/A"}</td>
              <td>{getVendorName(booking.vendor)}</td>
              <td>{booking.serviceDescription || "—"}</td>
              <td>
                <span
                  className={`book-badge ${getStatusBadgeClass(
                    booking.bookingStatus
                  )}`}
                >
                  {booking.bookingStatus ?? "N/A"}
                </span>
              </td>
              <td>{formatDateTime(booking.startDateTime)}</td>
              <td>{formatDateTime(booking.endDateTime)}</td>

              {canManage && (
                <td className="book-actions-cell">
                  <div className="book-actions-stack">
                    <button
                      className="book-btn book-btn-success"
                      type="button"
                      onClick={() => handleConfirmBooking(booking.bookingId)}
                    >
                      Confirm
                    </button>

                    <button
                      className="book-btn book-btn-warning"
                      type="button"
                      onClick={() => handleCancelBooking(booking.bookingId)}
                    >
                      Cancel
                    </button>

                    <button
                      className="book-btn book-btn-secondary"
                      type="button"
                      onClick={() => handleCompleteBooking(booking.bookingId)}
                    >
                      Complete
                    </button>
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