import { useEffect, useState } from "react";
import "./ScheduleItems.css";
import ScheduleItemForm from "./ScheduleItemForm";
import ScheduleItemsTable from "./ScheduleItemsTable";
import ScheduleCalendarView from "./ScheduleCalendarView";

const API = "http://localhost:8080";

// Decodes the role claim from the JWT payload without a library
function getRoleFromToken(token) {
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return payload.role || "";
  } catch {
    return "";
  }
}

const emptyForm = {
  eventId: "",
  venueId: "",
  title: "",
  description: "",
  startDateTime: "",
  endDateTime: "",
  type: "",
};

// datetime-local input gives "2024-01-15T10:30" but backend needs "2024-01-15T10:30:00"
function withSeconds(dt) {
  if (!dt) return dt;
  return dt.length === 16 ? dt + ":00" : dt;
}

// backend returns "2024-01-15T10:30:00" but datetime-local input needs "2024-01-15T10:30"
function trimSeconds(dt) {
  if (!dt) return "";
  return dt.length >= 16 ? dt.slice(0, 16) : dt;
}

const PREVIEW_ROLES = ["ADMIN", "ORGANIZER", "STAFF", "PARTICIPANT"];

function canEditForRole(r) {
  return r === "ROLE_ADMIN" || r === "ROLE_ORGANIZER" || r === "ADMIN" || r === "ORGANIZER";
}

export default function ScheduleItemsPage() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [events, setEvents] = useState([]);
  const [venues, setVenues] = useState([]);
  const [viewMode, setViewMode] = useState("table");
  const [previewRole, setPreviewRole] = useState(null);

  const token = localStorage.getItem("token");
  const realRole = getRoleFromToken(token);
  const realIsAdmin = realRole === "ADMIN" || realRole === "ROLE_ADMIN";
  const effectiveRole = realIsAdmin && previewRole ? previewRole : realRole;
  const canEdit = canEditForRole(effectiveRole);

  useEffect(() => {
    fetchItems();
    fetchEvents();
    fetchVenues();
  }, []);

  async function fetchItems() {
    try {
      setLoading(true);
      setError("");
      const res = await fetch(`${API}/scheduleItems/my`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!res.ok) throw new Error("Failed to load schedule items");
      const data = await res.json();
      setItems(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function fetchEvents() {
    try {
      const res = await fetch(`${API}/events`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (res.ok) setEvents(await res.json());
    } catch {
      // non-blocking — form still works if this fails
    }
  }

  async function fetchVenues() {
    try {
      const res = await fetch(`${API}/venues`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (res.ok) setVenues(await res.json());
    } catch {
      // non-blocking — form still works if this fails
    }
  }

  function handleFieldChange(field, value) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  function openCreateForm() {
    setEditingItem(null);
    setForm(emptyForm);
    setShowForm(true);
    setMessage("");
    setError("");
  }

  function handleCancel() {
    setShowForm(false);
    setEditingItem(null);
    setForm(emptyForm);
  }

  function startEditing(item) {
    setEditingItem(item);
    setForm({
      eventId: item.eventId ?? "",
      venueId: item.venueId ?? "",
      title: item.title ?? "",
      description: item.description ?? "",
      startDateTime: trimSeconds(item.startDateTime),
      endDateTime: trimSeconds(item.endDateTime),
      type: item.type ?? "",
    });
    setShowForm(true);
    setMessage("");
    setError("");
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setMessage("");
    setError("");

    const params = new URLSearchParams({
      ...form,
      startDateTime: withSeconds(form.startDateTime),
      endDateTime: withSeconds(form.endDateTime),
    });

    try {
      const url = editingItem
        ? `${API}/scheduleItems/${editingItem.id}?${params}`
        : `${API}/scheduleItems?${params}`;
      const method = editingItem ? "PUT" : "POST";

      const res = await fetch(url, {
        method,
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!res.ok) throw new Error(editingItem ? "Failed to update schedule item" : "Failed to create schedule item");

      setMessage(editingItem ? "Schedule item updated." : "Schedule item created.");
      setShowForm(false);
      setEditingItem(null);
      setForm(emptyForm);
      fetchItems();
    } catch (err) {
      setError(err.message);
    }
  }

  async function handleDelete(id) {
    setMessage("");
    setError("");
    try {
      const res = await fetch(`${API}/scheduleItems/${id}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!res.ok) throw new Error("Failed to delete schedule item");
      setMessage("Schedule item deleted.");
      fetchItems();
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="schedule-page">
      {realIsAdmin && (
        <section className="schedule-section">
          <h3 className="schedule-section-title">Preview Page As</h3>
          <div className="schedule-form">
            <div className="schedule-form-grid">
              <div className="schedule-form-group">
                <label className="schedule-label" htmlFor="preview-role-select">
                  Role Preview
                </label>
                <select
                  id="preview-role-select"
                  className="schedule-select"
                  value={previewRole ?? ""}
                  onChange={(e) => {
                    setPreviewRole(e.target.value || null);
                    setShowForm(false);
                  }}
                >
                  <option value="">My Real Role ({realRole || "unknown"})</option>
                  {PREVIEW_ROLES.map((r) => (
                    <option key={r} value={r}>{r}</option>
                  ))}
                </select>
              </div>
            </div>
          </div>
        </section>
      )}

      {message && <div className="schedule-message success">{message}</div>}
      {error && <div className="schedule-message error">{error}</div>}

      <div style={{ display: "flex", gap: "0.75rem", marginBottom: "1.25rem", flexWrap: "wrap" }}>
        {!showForm && canEdit && (
          <button className="schedule-btn schedule-btn-success" onClick={openCreateForm}>
            + Add Schedule Item
          </button>
        )}
        <button
          className={`schedule-btn ${viewMode === "table" ? "schedule-btn-primary" : "schedule-btn-secondary"}`}
          onClick={() => setViewMode("table")}
        >
          Table View
        </button>
        <button
          className={`schedule-btn ${viewMode === "calendar" ? "schedule-btn-primary" : "schedule-btn-secondary"}`}
          onClick={() => setViewMode("calendar")}
        >
          Calendar View
        </button>
      </div>

      {showForm && (
        <ScheduleItemForm
          form={form}
          onFieldChange={handleFieldChange}
          onSubmit={handleSubmit}
          onCancel={handleCancel}
          isEditing={!!editingItem}
          events={events}
          venues={venues}
        />
      )}

      {viewMode === "table" ? (
        <ScheduleItemsTable
          loading={loading}
          items={items}
          onEdit={startEditing}
          onDelete={handleDelete}
          canEdit={canEdit}
        />
      ) : (
        <ScheduleCalendarView items={items} />
      )}
    </div>
  );
}
