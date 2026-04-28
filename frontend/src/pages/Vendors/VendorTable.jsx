import { useState } from "react";
import "./Vendors.css";

export default function VendorTable({
  vendors,
  canManage,
  handleUpdateVendor,
  handleDeactivateVendor,
  handleDeleteVendor,
}) {
  const [editingId, setEditingId] = useState(null);
  const [editName, setEditName] = useState("");
  const [editContactName, setEditContactName] = useState("");
  const [editContactEmail, setEditContactEmail] = useState("");
  const [editContactPhone, setEditContactPhone] = useState("");
  const [editAvailability, setEditAvailability] = useState("");
  const [editStreet, setEditStreet] = useState("");
  const [editCity, setEditCity] = useState("");
  const [editState, setEditState] = useState("");
  const [editZipCode, setEditZipCode] = useState("");
  const [editCountry, setEditCountry] = useState("");
  const [saving, setSaving] = useState(false);

  function startEdit(vendor) {
    setEditingId(vendor.id);
    setEditName(vendor.name ?? "");
    setEditContactName(vendor.contactName ?? "");
    setEditContactEmail(vendor.contactEmail ?? "");
    setEditContactPhone(vendor.contactPhone ?? "");
    setEditAvailability(vendor.availability ?? "");
    setEditStreet(vendor.address?.street ?? "");
    setEditCity(vendor.address?.city ?? "");
    setEditState(vendor.address?.state ?? "");
    setEditZipCode(vendor.address?.zipCode ?? "");
    setEditCountry(vendor.address?.country ?? "");
  }

  function cancelEdit() {
    setEditingId(null);
  }

  async function commitEdit(vendor) {
    setSaving(true);

    const updatedVendor = {
      name: editName,
      contactName: editContactName,
      contactEmail: editContactEmail,
      contactPhone: editContactPhone,
      availability: editAvailability,
      active: vendor.active,
      address: {
        street: editStreet,
        city: editCity,
        state: editState,
        zipCode: editZipCode,
        country: editCountry,
      },
    };

    await handleUpdateVendor(vendor.id, updatedVendor);
    setSaving(false);
    setEditingId(null);
  }

  function formatAddress(address) {
    if (!address) return "—";

    return [
      address.street,
      address.city,
      address.state,
      address.zipCode,
      address.country,
    ]
      .filter(Boolean)
      .join(", ");
  }

  return (
    <div className="vnd-table-wrapper">
      <table className="vnd-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Contact Name</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Address</th>
            <th>Availability</th>
            <th>Status</th>
            {canManage && <th>Actions</th>}
          </tr>
        </thead>

        <tbody>
          {vendors.map((vendor) => (
            <>
              <tr key={vendor.id}>
                <td>{vendor.id ?? "N/A"}</td>

                <td>{vendor.name ?? "—"}</td>

                <td>{vendor.contactName ?? "—"}</td>

                <td>{vendor.contactEmail ?? "—"}</td>

                <td>{vendor.contactPhone ?? "—"}</td>

                <td>{formatAddress(vendor.address)}</td>

                <td>{vendor.availability || "—"}</td>

                <td>
                  <span
                    className={`vnd-badge ${vendor.active ? "active" : "inactive"}`}
                  >
                    {vendor.active ? "Active" : "Inactive"}
                  </span>
                </td>

                {canManage && (
                  <td className="vnd-actions-cell">
                    <div className="vnd-actions-stack">
                      <button
                        className="vnd-btn vnd-btn-secondary"
                        type="button"
                        onClick={() => startEdit(vendor)}
                      >
                        Edit
                      </button>

                      {vendor.active && (
                        <button
                          className="vnd-btn vnd-btn-warning"
                          type="button"
                          onClick={() => handleDeactivateVendor(vendor.id)}
                        >
                          Deactivate
                        </button>
                      )}

                      <button
                        className="vnd-btn vnd-btn-danger"
                        type="button"
                        onClick={() => handleDeleteVendor(vendor.id)}
                      >
                        Delete
                      </button>
                    </div>
                  </td>
                )}
              </tr>

              {editingId === vendor.id && (
                <tr key={`${vendor.id}-edit`}>
                  <td colSpan={canManage ? 9 : 8}>
                    <div className="vnd-inline-edit">
                      <div className="vnd-inline-edit-grid">
                        <div className="vnd-form-group">
                          <label className="vnd-label">Name</label>
                          <input
                            className="vnd-input"
                            value={editName}
                            onChange={(e) => setEditName(e.target.value)}
                          />
                        </div>

                        <div className="vnd-form-group">
                          <label className="vnd-label">Contact Name</label>
                          <input
                            className="vnd-input"
                            value={editContactName}
                            onChange={(e) => setEditContactName(e.target.value)}
                          />
                        </div>

                        <div className="vnd-form-group">
                          <label className="vnd-label">Email</label>
                          <input
                            className="vnd-input"
                            type="email"
                            value={editContactEmail}
                            onChange={(e) => setEditContactEmail(e.target.value)}
                          />
                        </div>

                        <div className="vnd-form-group">
                          <label className="vnd-label">Phone</label>
                          <input
                            className="vnd-input"
                            value={editContactPhone}
                            onChange={(e) => setEditContactPhone(e.target.value)}
                          />
                        </div>

                        <div className="vnd-form-group">
                          <label className="vnd-label">Availability</label>
                          <input
                            className="vnd-input"
                            value={editAvailability}
                            onChange={(e) => setEditAvailability(e.target.value)}
                          />
                        </div>

                        <div className="vnd-form-group">
                          <label className="vnd-label">Street</label>
                          <input
                            className="vnd-input"
                            value={editStreet}
                            onChange={(e) => setEditStreet(e.target.value)}
                          />
                        </div>

                        <div className="vnd-form-group">
                          <label className="vnd-label">City</label>
                          <input
                            className="vnd-input"
                            value={editCity}
                            onChange={(e) => setEditCity(e.target.value)}
                          />
                        </div>

                        <div className="vnd-form-group">
                          <label className="vnd-label">State</label>
                          <input
                            className="vnd-input"
                            value={editState}
                            onChange={(e) => setEditState(e.target.value)}
                          />
                        </div>

                        <div className="vnd-form-group">
                          <label className="vnd-label">Zip Code</label>
                          <input
                            className="vnd-input"
                            value={editZipCode}
                            onChange={(e) => setEditZipCode(e.target.value)}
                          />
                        </div>

                        <div className="vnd-form-group">
                          <label className="vnd-label">Country</label>
                          <input
                            className="vnd-input"
                            value={editCountry}
                            onChange={(e) => setEditCountry(e.target.value)}
                          />
                        </div>
                      </div>

                      <div className="vnd-actions-row">
                        <button
                          className="vnd-btn vnd-btn-primary"
                          type="button"
                          disabled={saving}
                          onClick={() => commitEdit(vendor)}
                        >
                          {saving ? "Saving..." : "Save Changes"}
                        </button>

                        <button
                          className="vnd-btn vnd-btn-outline"
                          type="button"
                          onClick={cancelEdit}
                        >
                          Cancel
                        </button>
                      </div>
                    </div>
                  </td>
                </tr>
              )}
            </>
          ))}
        </tbody>
      </table>
    </div>
  );
}
