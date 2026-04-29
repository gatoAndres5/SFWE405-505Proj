import "./EventStyle.css";
import { useEffect, useState } from "react";
import axios from "axios";

export default function EventsPage() {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [activeTab, setActiveTab] = useState("ALL");
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [isCreating, setIsCreating] = useState(false);

  const [eventDetails, setEventDetails] = useState(null);
  const [participants, setParticipants] = useState([]);
  const [vendors, setVendors] = useState([]);
  const [venues, setVenues] = useState([]);
  const [scheduleItems, setScheduleItems] = useState([]);

  const [organizerUserId, setOrganizerUserId] = useState("");
  const [staffUserId, setStaffUserId] = useState("");
  const [organizers, setOrganizers] = useState([]);
  const [staffUsers, setStaffUsers] = useState([])

  const [form, setForm] = useState({
    name: "",
    description: "",
    startDateTime: "",
    endDateTime: "",
  });

  const token = localStorage.getItem("token");

  const getRole = () => {
    const savedRole = localStorage.getItem("role");
    if (savedRole) return savedRole;

    if (!token) return "";

    try {
      const payload = JSON.parse(atob(token.split(".")[1]));
      return payload.role || payload.authority || "";
    } catch {
      return "";
    }
  };


  const fetchAssignableUsers = async () => {
    try {
      if (isAdmin) {
        const organizerRes = await axios.get(
          "http://localhost:8080/events/organizers",
          config
        );
        setOrganizers(organizerRes.data);
      }

      if (canManageEvents) {
        const staffRes = await axios.get(
          "http://localhost:8080/events/staff",
          config
        );
        setStaffUsers(staffRes.data);
      }
    } catch (e) {
      console.error(e);
      setError("Failed to load assignable users");
    }
  };


  const role = getRole();
  const isAdmin = role === "ADMIN";
  const isOrganizer = role === "ORGANIZER";
  const canManageEvents = isAdmin || isOrganizer;

  const config = {
    headers: { Authorization: `Bearer ${token}` },
  };

  const fetchEvents = async () => {
    try {
      setLoading(true);
      const res = await axios.get("http://localhost:8080/events", config);
      setEvents(res.data);
    } catch (e) {
      console.error(e);
      setError("Failed to load events");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchEvents();
    fetchAssignableUsers();
  }, []);

  const filteredEvents = events.filter((e) => {
    if (activeTab === "ALL") return true;
    return e.status === activeTab;
  });

  const openCreate = () => {
    setIsCreating(true);
    setSelectedEvent(null);
    setOrganizerUserId("");
    setStaffUserId("");
    setForm({
      name: "",
      description: "",
      startDateTime: "",
      endDateTime: "",
    });
  };

  const openEdit = (event) => {
    setIsCreating(false);
    setSelectedEvent(event);
    setOrganizerUserId("");
    setStaffUserId("");

    setForm({
      name: event.name || "",
      description: event.description || "",
      startDateTime: event.startDateTime || "",
      endDateTime: event.endDateTime || "",
    });
  };

  const closeEditor = () => {
    setSelectedEvent(null);
    setIsCreating(false);
    setOrganizerUserId("");
    setStaffUserId("");
  };

  const saveEvent = async () => {
    try {
      if (selectedEvent) {
        await axios.put(
          `http://localhost:8080/events/${selectedEvent.id}`,
          form,
          config
        );
      } else {
        await axios.post("http://localhost:8080/events", form, config);
      }

      closeEditor();
      fetchEvents();
    } catch (e) {
      console.error(e);
      setError("Failed to save event");
    }
  };

  const cancelEvent = async (id) => {
    try {
      await axios.put(`http://localhost:8080/events/${id}/cancel`, {}, config);
      fetchEvents();
    } catch (e) {
      console.error(e);
      setError("Failed to cancel event");
    }
  };

  const activateEvent = async (id) => {
    try {
      await axios.put(`http://localhost:8080/events/${id}/activate`, {}, config);
      fetchEvents();
    } catch (e) {
      console.error(e);
      setError("Failed to activate event");
    }
  };

  const getEventDetails = async (id) => {
    try {
      const res = await axios.get(`http://localhost:8080/events/${id}`, config);
      setEventDetails(res.data);

      fetchParticipants(id);
      fetchVendors(id);
      fetchVenues(id);
      fetchScheduleItems(id);
    } catch (e) {
      console.error(e);
      setError("Failed to load event details");
    }
  };

  const fetchParticipants = async (id) => {
    const res = await axios.get(
      `http://localhost:8080/events/${id}/participants`,
      config
    );
    setParticipants(res.data);
  };

  const fetchVendors = async (id) => {
    const res = await axios.get(
      `http://localhost:8080/events/${id}/vendors`,
      config
    );
    setVendors(res.data);
  };

  const fetchVenues = async (id) => {
    const res = await axios.get(
      `http://localhost:8080/events/${id}/venues`,
      config
    );
    setVenues(res.data);
  };

  const fetchScheduleItems = async (id) => {
    const res = await axios.get(
      `http://localhost:8080/events/${id}/scheduleitems`,
      config
    );
    setScheduleItems(res.data);
  };

  const removeVenue = async (eventId, venueId) => {
    try {
      await axios.delete(
        `http://localhost:8080/events/${eventId}/venues/${venueId}`,
        config
      );
      fetchVenues(eventId);
    } catch (e) {
      console.error(e);
      setError("Failed to remove venue");
    }
  };

  const removeScheduleItem = async (eventId, itemId) => {
    try {
      await axios.delete(
        `http://localhost:8080/events/${eventId}/schedule/${itemId}`,
        config
      );
      fetchScheduleItems(eventId);
    } catch (e) {
      console.error(e);
      setError("Failed to remove schedule item");
    }
  };

  const assignOrganizer = async (eventId) => {
    try {
      await axios.post(
        `http://localhost:8080/events/${eventId}/assign/organizer/${organizerUserId}`,
        {},
        config
      );
      setOrganizerUserId("");
      setError("");
      fetchEvents();
    } catch (e) {
      console.error(e);
      setError("Failed to assign organizer");
    }
  };

  const assignStaff = async (eventId) => {
    try {
      await axios.post(
        `http://localhost:8080/events/${eventId}/assign/staff/${staffUserId}`,
        {},
        config
      );
      setStaffUserId("");
      setError("");
      fetchEvents();
    } catch (e) {
      console.error(e);
      setError("Failed to assign staff");
    }
  };

  return (
    <div className="page">
      <div className="header">
        <h1 className="title">Events Dashboard</h1>

        {canManageEvents && (
          <button onClick={openCreate} className="primaryBtn">
            + Create Event
          </button>
        )}
      </div>

      <div className="tabBar">
        {["ALL", "ACTIVE", "CANCELLED", "DRAFT"].map((tab) => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`tab ${activeTab === tab ? "tabActive" : ""}`}
          >
            {tab}
          </button>
        ))}
      </div>

      {error && <div className="error">{error}</div>}

      {(isCreating || selectedEvent) && canManageEvents && (
        <div className="editor">
          <h3>{isCreating ? "Create Event" : "Edit Event"}</h3>

          <input
            className="input"
            placeholder="Name"
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
          />

          <input
            className="input"
            placeholder="Description"
            value={form.description}
            onChange={(e) => setForm({ ...form, description: e.target.value })}
          />

          <input
            className="input"
            type="datetime-local"
            value={form.startDateTime}
            onChange={(e) =>
              setForm({ ...form, startDateTime: e.target.value })
            }
          />

          <input
            className="input"
            type="datetime-local"
            value={form.endDateTime}
            onChange={(e) =>
              setForm({ ...form, endDateTime: e.target.value })
            }
          />

          <div className="actions">
            <button onClick={closeEditor} className="secondaryBtn">
              Cancel
            </button>

            <button onClick={saveEvent} className="primaryBtn">
              Save
            </button>
          </div>

          {selectedEvent && (
            <>
              {isAdmin && (
                <>
                  <h4>Assign Organizer</h4>
                  <div className="assignBox">
                    <select
                      className="input"
                      value={organizerUserId}
                      onChange={(e) => setOrganizerUserId(e.target.value)}
                    >
                      <option value="">Select Organizer</option>
                      {organizers.map((u) => (
                        <option key={u.id} value={u.id}>
                          {u.username}
                        </option>
                      ))}
                    </select>
                    <button
                      className="primaryBtn"
                      onClick={() => assignOrganizer(selectedEvent.id)}
                    >
                      Assign Organizer
                    </button>
                  </div>
                </>
              )}

              <h4>Assign Staff</h4>
              <div className="assignBox">
                <select
                  className="input"
                  value={staffUserId}
                  onChange={(e) => setStaffUserId(e.target.value)}
                >
                  <option value="">Select Staff</option>
                  {staffUsers.map((u) => (
                    <option key={u.id} value={u.id}>
                      {u.username}
                    </option>
                  ))}
                </select>
                <button
                  className="primaryBtn"
                  onClick={() => assignStaff(selectedEvent.id)}
                >
                  Assign Staff
                </button>
              </div>
            </>
          )}
        </div>
      )}

      {eventDetails && (
        <div className="editor">
          <h3>Event Details</h3>

          <p>
            <b>{eventDetails.name}</b>
          </p>
          <p>{eventDetails.description}</p>

          <h4>Venues</h4>
          {venues.length === 0 ? (
            <p className="muted">No venues added.</p>
          ) : (
            venues.map((v) => (
              <div key={v.id} className="row">
                {v.name}
                {canManageEvents && (
                  <button
                    className="dangerBtn"
                    onClick={() => removeVenue(eventDetails.id, v.id)}
                  >
                    Remove
                  </button>
                )}
              </div>
            ))
          )}

          <h4>Participants</h4>
          {participants.length === 0 ? (
            <p className="muted">No participants yet.</p>
          ) : (
            participants.map((p) => <div key={p.id}>{p.name}</div>)
          )}

          <h4>Vendors</h4>
          {vendors.length === 0 ? (
            <p className="muted">No vendors added.</p>
          ) : (
            vendors.map((v) => <div key={v.id}>{v.name}</div>)
          )}

          <h4>Schedule</h4>
          {scheduleItems.length === 0 ? (
            <p className="muted">No schedule items yet.</p>
          ) : (
            scheduleItems.map((s) => (
              <div key={s.id} className="row">
                {s.name}
                {canManageEvents && (
                  <button
                    className="dangerBtn"
                    onClick={() => removeScheduleItem(eventDetails.id, s.id)}
                  >
                    Remove
                  </button>
                )}
              </div>
            ))
          )}

          <button
            className="secondaryBtn"
            onClick={() => setEventDetails(null)}
          >
            Close
          </button>
        </div>
      )}

      {loading ? (
        <p>Loading events...</p>
      ) : (
        <div className="grid">
          {filteredEvents.map((e) => (
            <div key={e.id} className="card">
              <div className="cardTop">
                <h3>{e.name}</h3>
                <span
                  className={`badge ${
                    e.status === "ACTIVE"
                      ? "badge-active"
                      : e.status === "CANCELLED"
                      ? "badge-cancelled"
                      : e.status === "DRAFT"
                      ? "badge-draft"
                      : ""
                  }`}
                >
                  {e.status}
                </span>
              </div>

              <p className="desc">{e.description}</p>

              <div className="actions">
                {canManageEvents && (
                  <button className="secondaryBtn" onClick={() => openEdit(e)}>
                    Edit
                  </button>
                )}

                <button
                  className="secondaryBtn"
                  onClick={() => getEventDetails(e.id)}
                >
                  View
                </button>

                {canManageEvents && e.status !== "ACTIVE" && (
                  <button
                    className="primaryBtn"
                    onClick={() => activateEvent(e.id)}
                  >
                    Activate
                  </button>
                )}

                {canManageEvents && e.status !== "CANCELLED" && (
                  <button
                    className="dangerBtn"
                    onClick={() => cancelEvent(e.id)}
                  >
                    Cancel
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}