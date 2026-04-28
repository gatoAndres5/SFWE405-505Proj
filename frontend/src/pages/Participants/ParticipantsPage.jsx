import { useEffect, useMemo, useState } from "react";
import "../AdminManagement/AdminManagement.css";

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const participantRoleOptions = ["ATTENDEE", "SPEAKER", "STAFF"];

const emptyParticipantForm = {
  firstName: "",
  lastName: "",
  email: "",
  phone: "",
  role: "ATTENDEE",
};

export default function ParticipantsPage() {
  const token = localStorage.getItem("token");

  const [participant, setParticipant] = useState(null);
  const [participants, setParticipants] = useState([]);
  const [form, setForm] = useState(emptyParticipantForm);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

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

  const role =
    jwtPayload?.role ||
    jwtPayload?.authorities ||
    jwtPayload?.userRole ||
    null;

  const username =
    jwtPayload?.sub ||
    jwtPayload?.username ||
    jwtPayload?.userName ||
    "User";

  const isParticipant = role === "PARTICIPANT";
  const canViewParticipants =
    role === "ADMIN" || role === "ORGANIZER" || role === "STAFF";

  useEffect(() => {
    async function loadPage() {
      try {
        setLoading(true);
        setError("");
        setMessage("");

        if (!token) {
          throw new Error("No authentication token found.");
        }

        if (isParticipant) {
          await loadMyParticipant();
        } else if (canViewParticipants) {
          await loadParticipants();
        } else {
          throw new Error("You do not have permission to view this page.");
        }
      } catch (err) {
        setError(err.message || "Failed to load participants page.");
      } finally {
        setLoading(false);
      }
    }

    loadPage();
  }, [token, role]);

  async function loadMyParticipant() {
    const response = await fetch(`${API_BASE_URL}/participants/me`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (response.status === 404) {
      setParticipant(null);
      setForm(emptyParticipantForm);
      return;
    }

    if (!response.ok) {
      throw new Error("Failed to load your participant profile.");
    }

    const data = await response.json();

    setParticipant(data);
    setForm({
      firstName: data.firstName || "",
      lastName: data.lastName || "",
      email: data.email || "",
      phone: data.phone || "",
      role: data.role || "ATTENDEE",
    });
  }

  async function loadParticipants() {
    const response = await fetch(`${API_BASE_URL}/participants`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      throw new Error("Failed to load participants.");
    }

    const data = await response.json();
    setParticipants(data ?? []);
  }

  function handleFieldChange(field, value) {
    setForm((prev) => ({
      ...prev,
      [field]: value,
    }));
  }

  async function handleSubmit(e) {
    e.preventDefault();

    try {
      setSaving(true);
      setError("");
      setMessage("");

      const isUpdating = Boolean(participant?.participantId);

      const response = await fetch(`${API_BASE_URL}/participants/me`, {
        method: isUpdating ? "PUT" : "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(form),
      });

      const data = await response.json().catch(() => null);

      if (!response.ok) {
        throw new Error(
          data?.message ||
            data?.error ||
            "Failed to save participant profile."
        );
      }

      setParticipant(data);
      setForm({
        firstName: data.firstName || "",
        lastName: data.lastName || "",
        email: data.email || "",
        phone: data.phone || "",
        role: data.role || "ATTENDEE",
      });

      setMessage(
        isUpdating
          ? "Participant profile updated successfully."
          : "Participant profile created successfully."
      );
    } catch (err) {
      setError(err.message || "Error saving participant profile.");
    } finally {
      setSaving(false);
    }
  }

  function getRoleBadgeClass(participantRole) {
    switch (participantRole) {
      case "STAFF":
        return "staff-role";
      case "SPEAKER":
        return "organizer-role";
      case "ATTENDEE":
      default:
        return "participant-role";
    }
  }

  if (loading) {
    return <p className="admin-muted">Loading participants...</p>;
  }

  return (
    <div className="admin-page">
      <h1 className="admin-page-title">Participants</h1>

      {isParticipant && (
        <p className="admin-page-subtitle">
          Welcome, {username}. Finish creating your participant profile or update
          your information.
        </p>
      )}

      {canViewParticipants && (
        <p className="admin-page-subtitle">
          View participant information stored in the system.
        </p>
      )}

      {message && <div className="admin-message success">{message}</div>}
      {error && <div className="admin-message error">{error}</div>}

      {isParticipant && (
        <section className="admin-section">
          <h2 className="admin-section-title">
            {participant ? "Edit Participant Profile" : "Finish Creating Account"}
          </h2>

          <p className="admin-section-subtitle">
            This creates or updates your participant entity in the database.
          </p>

          <form className="admin-form" onSubmit={handleSubmit}>
            <div className="admin-form-grid">
              <div className="admin-form-group">
                <label className="admin-label">First Name</label>
                <input
                  className="admin-input"
                  type="text"
                  value={form.firstName}
                  onChange={(e) =>
                    handleFieldChange("firstName", e.target.value)
                  }
                  required
                />
              </div>

              <div className="admin-form-group">
                <label className="admin-label">Last Name</label>
                <input
                  className="admin-input"
                  type="text"
                  value={form.lastName}
                  onChange={(e) =>
                    handleFieldChange("lastName", e.target.value)
                  }
                  required
                />
              </div>

              <div className="admin-form-group">
                <label className="admin-label">Email</label>
                <input
                  className="admin-input"
                  type="email"
                  value={form.email}
                  onChange={(e) => handleFieldChange("email", e.target.value)}
                  required
                />
              </div>

              <div className="admin-form-group">
                <label className="admin-label">Phone</label>
                <input
                  className="admin-input"
                  type="text"
                  value={form.phone}
                  onChange={(e) => handleFieldChange("phone", e.target.value)}
                />
              </div>

              <div className="admin-form-group">
                <label className="admin-label">Requested Role</label>
                <select
                  className="admin-select"
                  value={form.role}
                  onChange={(e) => handleFieldChange("role", e.target.value)}
                >
                  {participantRoleOptions.map((option) => (
                    <option key={option} value={option}>
                      {option}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="admin-actions-row">
              <button
                className="admin-btn admin-btn-primary"
                type="submit"
                disabled={saving}
              >
                {saving
                  ? "Saving..."
                  : participant
                    ? "Update Profile"
                    : "Create Profile"}
              </button>
            </div>
          </form>
        </section>
      )}

      {canViewParticipants && (
        <section className="admin-section">
          <h2 className="admin-section-title">Participant Records</h2>
          <p className="admin-section-subtitle">
            Admins, organizers, and staff can view participant information.
          </p>

          <div className="admin-table-wrapper">
            <table className="admin-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Phone</th>
                  <th>Role</th>
                  <th>Active</th>
                </tr>
              </thead>

              <tbody>
                {participants.length === 0 ? (
                  <tr>
                    <td colSpan="6" className="admin-empty">
                      No participants found.
                    </td>
                  </tr>
                ) : (
                  participants.map((p) => (
                    <tr key={p.participantId}>
                      <td>{p.participantId}</td>
                      <td>
                        {p.firstName} {p.lastName}
                      </td>
                      <td>{p.email}</td>
                      <td>{p.phone || "N/A"}</td>
                      <td>
                        <span
                          className={`admin-badge ${getRoleBadgeClass(p.role)}`}
                        >
                          {p.role}
                        </span>
                      </td>
                      <td>
                        <span
                          className={`admin-badge ${
                            p.active ? "enabled" : "disabled"
                          }`}
                        >
                          {p.active ? "Active" : "Inactive"}
                        </span>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </section>
      )}
    </div>
  );
}