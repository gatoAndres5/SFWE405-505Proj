import "./Vendors.css";

export default function VendorForm({
  name,
  setName,
  contactName,
  setContactName,
  contactEmail,
  setContactEmail,
  contactPhone,
  setContactPhone,
  availability,
  setAvailability,
  street,
  setStreet,
  city,
  setCity,
  state,
  setState,
  zipCode,
  setZipCode,
  country,
  setCountry,
  submitting,
  handleCreateVendor,
}) {
  return (
    <form className="vnd-form" onSubmit={handleCreateVendor}>
      <div className="vnd-form-grid">
        <div className="vnd-form-group">
          <label className="vnd-label" htmlFor="vndName">
            Vendor Name
          </label>
          <input
            className="vnd-input"
            id="vndName"
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Acme Catering Co."
            required
          />
        </div>

        <div className="vnd-form-group">
          <label className="vnd-label" htmlFor="vndContactName">
            Contact Name
          </label>
          <input
            className="vnd-input"
            id="vndContactName"
            type="text"
            value={contactName}
            onChange={(e) => setContactName(e.target.value)}
            placeholder="Jane Smith"
            required
          />
        </div>

        <div className="vnd-form-group">
          <label className="vnd-label" htmlFor="vndContactEmail">
            Contact Email
          </label>
          <input
            className="vnd-input"
            id="vndContactEmail"
            type="email"
            value={contactEmail}
            onChange={(e) => setContactEmail(e.target.value)}
            placeholder="jane@acme.com"
            required
          />
        </div>

        <div className="vnd-form-group">
          <label className="vnd-label" htmlFor="vndContactPhone">
            Contact Phone
          </label>
          <input
            className="vnd-input"
            id="vndContactPhone"
            type="text"
            value={contactPhone}
            onChange={(e) => setContactPhone(e.target.value)}
            placeholder="555-867-5309"
            required
          />
        </div>

        <div className="vnd-form-group full-width">
          <label className="vnd-label" htmlFor="vndAvailability">
            Availability Notes
          </label>
          <textarea
            className="vnd-textarea"
            id="vndAvailability"
            value={availability}
            onChange={(e) => setAvailability(e.target.value)}
            placeholder="e.g. Weekdays only, 8am–6pm"
            rows={3}
          />
        </div>

        <p className="vnd-address-heading">Address</p>

        <div className="vnd-form-group full-width">
          <label className="vnd-label" htmlFor="vndStreet">
            Street
          </label>
          <input
            className="vnd-input"
            id="vndStreet"
            type="text"
            value={street}
            onChange={(e) => setStreet(e.target.value)}
            placeholder="123 Main St"
            required
          />
        </div>

        <div className="vnd-form-group">
          <label className="vnd-label" htmlFor="vndCity">
            City
          </label>
          <input
            className="vnd-input"
            id="vndCity"
            type="text"
            value={city}
            onChange={(e) => setCity(e.target.value)}
            placeholder="Springfield"
            required
          />
        </div>

        <div className="vnd-form-group">
          <label className="vnd-label" htmlFor="vndState">
            State
          </label>
          <input
            className="vnd-input"
            id="vndState"
            type="text"
            value={state}
            onChange={(e) => setState(e.target.value)}
            placeholder="IL"
            required
          />
        </div>

        <div className="vnd-form-group">
          <label className="vnd-label" htmlFor="vndZipCode">
            Zip Code
          </label>
          <input
            className="vnd-input"
            id="vndZipCode"
            type="text"
            value={zipCode}
            onChange={(e) => setZipCode(e.target.value)}
            placeholder="62701"
            required
          />
        </div>

        <div className="vnd-form-group">
          <label className="vnd-label" htmlFor="vndCountry">
            Country
          </label>
          <input
            className="vnd-input"
            id="vndCountry"
            type="text"
            value={country}
            onChange={(e) => setCountry(e.target.value)}
            placeholder="USA"
            required
          />
        </div>
      </div>

      <div className="vnd-actions-row">
        <button
          className="vnd-btn vnd-btn-primary"
          type="submit"
          disabled={submitting}
        >
          {submitting ? "Creating..." : "Create Vendor"}
        </button>
      </div>
    </form>
  );
}
