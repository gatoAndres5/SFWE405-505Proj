import { useEffect, useState } from "react";
import "./ScheduleItems.css";
import ScheduleItemForm from "./ScheduleItemForm";
import ScheduleItemsTable from "./ScheduleItemsTable";

const API = "http://localhost:8080";

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

export default function ScheduleItemsPage() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [form, setForm] = useState(emptyForm);

  const token = localStorage.getItem("token");

  useEffect(() => {
    fetchItems();
  }, []);

  async function fetchItems() {
    try {
      setLoading(true);
      setError("");
      const res = await fetch(`${API}/scheduleItems`, {
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
      <h1 className="schedule-page-title">Schedule Items</h1>
      <p className="schedule-page-subtitle">
        Manage all schedule items across events and venues.
      </p>

      {message && <div className="schedule-message success">{message}</div>}
      {error && <div className="schedule-message error">{error}</div>}

      {!showForm && (
        <div style={{ marginBottom: "1.25rem" }}>
          <button className="schedule-btn schedule-btn-success" onClick={openCreateForm}>
            + Add Schedule Item
          </button>
        </div>
      )}

      {showForm && (
        <ScheduleItemForm
          form={form}
          onFieldChange={handleFieldChange}
          onSubmit={handleSubmit}
          onCancel={handleCancel}
          isEditing={!!editingItem}
        />
      )}

      <ScheduleItemsTable
        loading={loading}
        items={items}
        onEdit={startEditing}
        onDelete={handleDelete}
      />
    </div>
  );
}
