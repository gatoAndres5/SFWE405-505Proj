import EventSummaryCard from "./EventSummaryCard";
import VendorSummaryCard from "./VendorSummaryCard";
import "./Bookings.css";

export default function BookingForm({
  form,
  setForm,
  handleCreateBooking,
  submitting,
  events,
  vendors,
  getEventLabel,
  getVendorLabel,
  selectedEvent,
  selectedVendor,
  formatDateTime,
}) {
  function updateField(field, value) {
    setForm((prev) => ({
      ...prev,
      [field]: value,
    }));
  }

  return (
    <form className="book-form" onSubmit={handleCreateBooking}>
      <div className="book-form-grid">
        <div className="book-form-group">
          <label className="book-label" htmlFor="bookingEvent">
            Event
          </label>
          <select
            id="bookingEvent"
            className="book-select"
            value={form.eventId}
            onChange={(e) => updateField("eventId", e.target.value)}
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

        <div className="book-form-group">
          <label className="book-label" htmlFor="bookingVendor">
            Vendor
          </label>
          <select
            id="bookingVendor"
            className="book-select"
            value={form.vendorId}
            onChange={(e) => updateField("vendorId", e.target.value)}
            required
          >
            <option value="">Select a vendor</option>
            {vendors.map((vendor) => (
              <option
                key={vendor.vendorId ?? vendor.id}
                value={vendor.vendorId ?? vendor.id}
              >
                {getVendorLabel(vendor)}
              </option>
            ))}
          </select>
        </div>

         

        <div className="book-form-group full-width">
            
            {(selectedEvent || selectedVendor) && (
            <div className="book-summary-grid">
            {selectedEvent &&(
                <EventSummaryCard
                event={selectedEvent}
                formatDateTime={formatDateTime}
            />
            )}
            {selectedVendor && <VendorSummaryCard vendor={selectedVendor} />}
            </div>
        )}
          <label className="book-label" htmlFor="serviceDescription">
            Service Description
          </label>
          <textarea
            id="serviceDescription"
            className="book-textarea"
            value={form.serviceDescription}
            onChange={(e) => updateField("serviceDescription", e.target.value)}
            placeholder="Describe the vendor service..."
            rows={3}
            required
          />
        </div>

        <div className="book-form-group">
          <label className="book-label" htmlFor="startDateTime">
            Start Time
          </label>
          <input
            id="startDateTime"
            className="book-input"
            type="datetime-local"
            value={form.startDateTime}
            onChange={(e) => updateField("startDateTime", e.target.value)}
            required
          />
        </div>

        <div className="book-form-group">
          <label className="book-label" htmlFor="endDateTime">
            End Time
          </label>
          <input
            id="endDateTime"
            className="book-input"
            type="datetime-local"
            value={form.endDateTime}
            onChange={(e) => updateField("endDateTime", e.target.value)}
            required
          />
          
        </div>
      </div>
      <div className="book-actions-row">
                <button
                className="book-btn book-btn-primary"
                type="submit"
                disabled={submitting}
                >
                {submitting ? "Submitting..." : "Create Booking"}
                </button>
            </div>

    </form>
  );
}