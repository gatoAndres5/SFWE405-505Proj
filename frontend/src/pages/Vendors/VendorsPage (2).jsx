import { useEffect, useMemo, useState } from "react";
import VendorRolePreviewPanel from "./VendorRolePreviewPanel";
import VendorForm from "./VendorForm";
import VendorTable from "./VendorTable";
import "./Vendors.css";

const API_BASE = "http://localhost:8080";

export default function VendorsPage() {
  const [vendors, setVendors] = useState([]);
  const [users, setUsers] = useState([]);
  const [previewUserEvents, setPreviewUserEvents] = useState([]);

  const [name, setName] = useState("");
  const [contactName, setContactName] = useState("");
  const [contactEmail, setContactEmail] = useState("");
  const [contactPhone, setContactPhone] = useState("");
  const [availability, setAvailability] = useState("");
  const [street, setStreet] = useState("");
  const [city, setCity] = useState("");
  const [state, setState] = useState("");
  const [zipCode, setZipCode] = useState("");
  const [country, setCountry] = useState("");

  const [rolePreview, setRolePreview] = useState("");
  const [organizerPreviewId, setOrganizerPreviewId] = useState("");
  const [staffPreviewId, setStaffPreviewId] = useState("");
  const [vendorSearch, setVendorSearch] = useState("");
  const [vendorStatusFilter, setVendorStatusFilter] = useState("");

  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const token = localStorage.getItem("token");

  function parseJwt(tokenValue) {
    try {
      if (!tokenValue) return null;
      const payload = tokenValue.split(".")[1];
      return JSON.parse(atob(payload));
    } catch {
      return null;
    }
  }

  const jwtPayload = useMemo(() => parseJwt(token), [token]);

  const realRole =
    jwtPayload?.role ||
    jwtPayload?.authorities ||
    jwtPayload?.userRole ||
    null;

  const realIsAdmin = realRole === "ADMIN";

  const effectiveRole = realIsAdmin && rolePreview ? rolePreview : realRole;

  const isAdmin = effectiveRole === "ADMIN";
  const isOrganizer = effectiveRole === "ORGANIZER";
  const isParticipant = effectiveRole === "PARTICIPANT";

  const canCreate = isAdmin || isOrganizer;
  const canManage = isAdmin || isOrganizer;

  const organizers = users.filter((user) => user.role === "ORGANIZER");
  const staffUsers = users.filter((user) => user.role === "STAFF");

  function getOrganizerLabel(organizer) {
    return `${organizer.id} - ${organizer.username}`;
  }

  function getStaffLabel(staff) {
    return `${staff.id} - ${staff.username}`;
  }

  async function apiRequest(url, options = {}) {
    const res = await fetch(url, {
      ...options,
      headers: {
        "Content-Type": "application/json",
        ...(options.headers || {}),
        Authorization: token ? `Bearer ${token}` : "",
      },
    });

    if (!res.ok) {
      const text = await res.text();
      throw new Error(text || "Request failed.");
    }

    const contentType = res.headers.get("content-type");
    if (contentType && contentType.includes("application/json")) {
      return res.json();
    }

    return null;
  }

  async function fetchVendors() {
    const data = await apiRequest(`${API_BASE}/vendors`);
    setVendors(data || []);
  }

  async function fetchUsers() {
    try {
      const res = await fetch(`${API_BASE}/users`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!res.ok) throw new Error("Failed to fetch users");

      const data = await res.json();
      setUsers(data || []);
    } catch (err) {
      console.error(err);
      setUsers([]);
    }
  }

  async function fetchPreviewUserEvents(userId) {
    if (!userId) {
      setPreviewUserEvents([]);
      return;
    }

    try {
      const data = await apiRequest(`${API_BASE}/users/${userId}/events`);
      setPreviewUserEvents(data || []);
    } catch (err) {
      console.error(err);
      setPreviewUserEvents([]);
    }
  }

  async function loadPageData() {
    setLoading(true);
    setError("");
    setMessage("");

    try {
      const requests = [fetchVendors()];

      if (realIsAdmin) {
        requests.push(fetchUsers());
      }

      await Promise.all(requests);
    } catch (err) {
      setError(err.message || "Failed to load page data.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadPageData();
  }, []);

  useEffect(() => {
    if (rolePreview !== "ORGANIZER") {
      setOrganizerPreviewId("");
    }

    if (rolePreview !== "STAFF") {
      setStaffPreviewId("");
    }
  }, [rolePreview]);

  useEffect(() => {
    if (!realIsAdmin) return;

    if (effectiveRole === "ORGANIZER" && organizerPreviewId) {
      fetchPreviewUserEvents(organizerPreviewId);
      return;
    }

    if (effectiveRole === "STAFF" && staffPreviewId) {
      fetchPreviewUserEvents(staffPreviewId);
      return;
    }

    setPreviewUserEvents([]);
  }, [realIsAdmin, effectiveRole, organizerPreviewId, staffPreviewId]);

  const previewUserEventIds = useMemo(() => {
    return previewUserEvents.map((event) => String(event.id));
  }, [previewUserEvents]);

  const visibleVendors = useMemo(() => {
    let filtered = vendors;

    if (vendorStatusFilter === "active") {
      filtered = filtered.filter((vendor) => vendor.active === true);
    }

    if (vendorStatusFilter === "inactive") {
      filtered = filtered.filter((vendor) => vendor.active === false);
    }

    if (vendorSearch.trim()) {
      const query = vendorSearch.trim().toLowerCase();

      filtered = filtered.filter((vendor) => {
        const vendorId = String(vendor.id ?? "").toLowerCase();
        const vendorName = String(vendor.name ?? "").toLowerCase();
        const contact = String(vendor.contactName ?? "").toLowerCase();
        const email = String(vendor.contactEmail ?? "").toLowerCase();
        const phone = String(vendor.contactPhone ?? "").toLowerCase();
        const vendorCity = String(vendor.address?.city ?? "").toLowerCase();
        const vendorState = String(vendor.address?.state ?? "").toLowerCase();

        return (
          vendorId.includes(query) ||
          vendorName.includes(query) ||
          contact.includes(query) ||
          email.includes(query) ||
          phone.includes(query) ||
          vendorCity.includes(query) ||
          vendorState.includes(query)
        );
      });
    }

    return filtered;
  }, [vendors, vendorStatusFilter, vendorSearch]);

  function clearForm() {
    setName("");
    setContactName("");
    setContactEmail("");
    setContactPhone("");
    setAvailability("");
    setStreet("");
    setCity("");
    setState("");
    setZipCode("");
    setCountry("");
  }

  async function handleCreateVendor(e) {
    e.preventDefault();
    setSubmitting(true);
    setError("");
    setMessage("");

    try {
      await apiRequest(`${API_BASE}/vendors`, {
        method: "POST",
        body: JSON.stringify({
          name,
          contactName,
          contactEmail,
          contactPhone,
          availability,
          address: {
            street,
            city,
            state,
            zipCode,
            country,
          },
        }),
      });

      setMessage("Vendor created successfully.");
      clearForm();
      await fetchVendors();
    } catch (err) {
      setError(err.message || "Failed to create vendor.");
    } finally {
      setSubmitting(false);
    }
  }

  async function handleUpdateVendor(id, updatedVendor) {
    setError("");
    setMessage("");

    try {
      await apiRequest(`${API_BASE}/vendors/${id}`, {
        method: "PUT",
        body: JSON.stringify(updatedVendor),
      });

      setMessage("Vendor updated successfully.");
      await fetchVendors();
    } catch (err) {
      setError(err.message || "Failed to update vendor.");
    }
  }

  async function handleDeactivateVendor(id) {
    setError("");
    setMessage("");

    try {
      await apiRequest(`${API_BASE}/vendors/${id}/deactivate`, {
        method: "PATCH",
      });

      setMessage("Vendor deactivated successfully.");
      await fetchVendors();
    } catch (err) {
      setError(err.message || "Failed to deactivate vendor.");
    }
  }

  async function handleDeleteVendor(id) {
    setError("");
    setMessage("");

    try {
      await apiRequest(`${API_BASE}/vendors/${id}`, {
        method: "DELETE",
      });

      setMessage("Vendor deleted successfully.");
      await fetchVendors();
    } catch (err) {
      setError(err.message || "Failed to delete vendor.");
    }
  }

  return (
    <div className="vnd-page">
      {message && <div className="vnd-message success">{message}</div>}
      {error && <div className="vnd-message error">{error}</div>}

      {realIsAdmin && (
        <section className="vnd-section">
          <h3 className="vnd-section-title">Preview Page As</h3>
          <VendorRolePreviewPanel
            realIsAdmin={realIsAdmin}
            rolePreview={rolePreview}
            setRolePreview={setRolePreview}
            organizerPreviewId={organizerPreviewId}
            setOrganizerPreviewId={setOrganizerPreviewId}
            organizers={organizers}
            getOrganizerLabel={getOrganizerLabel}
            staffPreviewId={staffPreviewId}
            setStaffPreviewId={setStaffPreviewId}
            staffUsers={staffUsers}
            getStaffLabel={getStaffLabel}
          />
        </section>
      )}

      {canCreate && (
        <section className="vnd-section">
          <h3 className="vnd-section-title">Create Vendor</h3>
          <VendorForm
            name={name}
            setName={setName}
            contactName={contactName}
            setContactName={setContactName}
            contactEmail={contactEmail}
            setContactEmail={setContactEmail}
            contactPhone={contactPhone}
            setContactPhone={setContactPhone}
            availability={availability}
            setAvailability={setAvailability}
            street={street}
            setStreet={setStreet}
            city={city}
            setCity={setCity}
            state={state}
            setState={setState}
            zipCode={zipCode}
            setZipCode={setZipCode}
            country={country}
            setCountry={setCountry}
            submitting={submitting}
            handleCreateVendor={handleCreateVendor}
          />
        </section>
      )}

      <section className="vnd-section">
        <h3 className="vnd-section-title">
          {isParticipant ? "Vendors for Your Events" : "Existing Vendors"}
        </h3>

        <div className="vnd-filter-bar">
          <div className="vnd-filter-item">
            <label className="vnd-label" htmlFor="vendorSearch">
              Search vendors
            </label>
            <input
              className="vnd-input"
              id="vendorSearch"
              type="text"
              value={vendorSearch}
              onChange={(e) => setVendorSearch(e.target.value)}
              placeholder="Search by name, email, phone, or city"
            />
          </div>

          <div className="vnd-filter-item">
            <label className="vnd-label" htmlFor="vendorStatusFilter">
              Filter by status
            </label>
            <select
              className="vnd-select"
              id="vendorStatusFilter"
              value={vendorStatusFilter}
              onChange={(e) => setVendorStatusFilter(e.target.value)}
            >
              <option value="">All statuses</option>
              <option value="active">Active</option>
              <option value="inactive">Inactive</option>
            </select>
          </div>
        </div>

        {loading ? (
          <p className="vnd-muted">Loading vendors...</p>
        ) : visibleVendors.length === 0 ? (
          <p className="vnd-table-empty">No vendors found.</p>
        ) : (
          <VendorTable
            vendors={visibleVendors}
            canManage={canManage}
            handleUpdateVendor={handleUpdateVendor}
            handleDeactivateVendor={handleDeactivateVendor}
            handleDeleteVendor={handleDeleteVendor}
          />
        )}
      </section>
    </div>
  );
}
