import "./Bookings.css";

export default function VendorSummaryCard({ vendor }) {
  if (!vendor) return null;

  function getAddressSummary(currentVendor) {
    const addressObj = currentVendor.address ?? currentVendor;

    const parts = [
      addressObj.street,
      addressObj.city,
      addressObj.state,
      addressObj.zipCode,
      addressObj.country,
    ].filter(Boolean);

    return parts.length ? parts.join(", ") : "No address available";
  }

  return (
    <div className="book-summary-card">
      <h4>{vendor.name ?? vendor.companyName ?? "Unnamed Vendor"}</h4>

      <p>
        <span className="book-muted">Contact:</span>{" "}
        {vendor.contactName ?? "No contact available"}
      </p>

      <p>
        <span className="book-muted">Email:</span>{" "}
        {vendor.contactEmail ?? "No email available"}
      </p>

      <p>
        <span className="book-muted">Phone:</span>{" "}
        {vendor.contactPhone ?? "No phone available"}
      </p>

      <p>
        <span className="book-muted">Availability:</span>{" "}
        {vendor.availability ?? "N/A"}
      </p>

      <p>
        <span className="book-muted">Status:</span>{" "}
        <span className={`book-badge ${vendor.active ? "confirmed" : "cancelled"}`}>
          {vendor.active ? "Active" : "Inactive"}
        </span>
      </p>

      <p className="book-small book-muted">
        {getAddressSummary(vendor)}
      </p>
    </div>
  );
}