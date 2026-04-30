import { useEffect, useState } from "react";
import axios from "axios";
import "./Venues.css";
import VenueForm from "./VenueForm";

const API_BASE = "http://localhost:8080";

const PREVIEW_ROLES = ["ADMIN", "ORGANIZER", "STAFF", "PARTICIPANT"];

function canEditForRole(r) {
  return r === "ROLE_ADMIN" || r === "ROLE_ORGANIZER" || r === "ADMIN" || r === "ORGANIZER";
}

const emptyVenueForm = {
  name: "",
  address: {
    street: "",
    city: "",
    state: "",
    zipCode: "",
    country: ""
  },
  capacity: "",
  contactName: "",
  contactEmail: "",
  contactPhone: ""
};

export default function VenuesPage() {
  const [venues, setVenues] = useState([]);
  const [events, setEvents] = useState([]);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [selectedVenue, setSelectedVenue] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [showVenueForm, setShowVenueForm] = useState(false);
  const [editingVenue, setEditingVenue] = useState(null);
  const [venueForm, setVenueForm] = useState(emptyVenueForm);
  const [submitting, setSubmitting] = useState(false);
  const [previewRole, setPreviewRole] = useState(null);

  const token = localStorage.getItem("token");

  // Get user role from token
  const getUserRole = () => {
    if (!token) return "";
    try {
      const payload = JSON.parse(atob(token.split(".")[1]));
      return payload.role || payload.authority || "";
    } catch {
      return "";
    }
  };

  const userRole = getUserRole();
  const isAdmin = userRole === "ADMIN" || userRole === "ROLE_ADMIN";
  const isOrganizer = userRole === "ORGANIZER" || userRole === "ROLE_ORGANIZER";
  const canManageVenues = isAdmin || isOrganizer;
  
  // Role preview functionality
  const realIsAdmin = isAdmin;
  const effectiveRole = realIsAdmin && previewRole ? previewRole : userRole;
  const canEdit = canEditForRole(effectiveRole);

  useEffect(() => {
    if (canManageVenues) {
      fetchVenues();
      fetchEvents();
    } else {
      fetchUserVenues();
    }
  }, []);

  const fetchVenues = async () => {
    try {
      setLoading(true);
      const response = await axios.get(`${API_BASE}/venues/list`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setVenues(response.data);
    } catch (error) {
      setError('Failed to fetch venues');
      console.error('Error fetching venues:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchEvents = async () => {
    try {
      const response = await axios.get(`${API_BASE}/events`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setEvents(response.data.filter(event => event.status === 'DRAFT' || event.status === 'ACTIVE'));
    } catch (error) {
      console.error('Error fetching events:', error);
    }
  };

  const fetchUserVenues = async () => {
    try {
      setLoading(true);
      // For participants, fetch events they're registered/assigned to
      const eventsResponse = await axios.get(`${API_BASE}/events`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      
      const userEvents = eventsResponse.data;
      const userVenues = [];
      
      // For each event, fetch its venues
      for (const event of userEvents) {
        try {
          const venuesResponse = await axios.get(`${API_BASE}/events/${event.id}/venues`, {
            headers: { Authorization: `Bearer ${token}` }
          });
          
          userVenues.push(...venuesResponse.data);
        } catch (venueError) {
          console.error(`Error fetching venues for event ${event.id}:`, venueError);
        }
      }
      
      // Remove duplicates by venue ID
      const uniqueVenues = userVenues.filter((venue, index, self) =>
        index === self.findIndex((v) => v.id === venue.id)
      );
      
      setVenues(uniqueVenues);
    } catch (error) {
      setError('Failed to fetch your venues');
      console.error('Error fetching user venues:', error);
    } finally {
      setLoading(false);
    }
  };

  // Venue management functions
  const openCreateVenueForm = () => {
    setEditingVenue(null);
    setVenueForm(emptyVenueForm);
    setShowVenueForm(true);
    setMessage("");
    setError("");
  };

  const startEditingVenue = (venue) => {
    setEditingVenue(venue);
    setVenueForm({
      id: venue.id,
      name: venue.name || "",
      address: {
        street: venue.address?.street || "",
        city: venue.address?.city || "",
        state: venue.address?.state || "",
        zipCode: venue.address?.zipCode || "",
        country: venue.address?.country || ""
      },
      capacity: venue.capacity || "",
      contactName: venue.contactName || "",
      contactEmail: venue.contactEmail || "",
      contactPhone: venue.contactPhone || ""
    });
    setShowVenueForm(true);
    setMessage("");
    setError("");
  };

  const handleVenueFormCancel = () => {
    setShowVenueForm(false);
    setEditingVenue(null);
    setVenueForm(emptyVenueForm);
  };

  const handleVenueFormSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError("");
    setMessage("");

    try {
      const venueData = {
        name: venueForm.name,
        address: venueForm.address,
        capacity: parseInt(venueForm.capacity),
        contactName: venueForm.contactName,
        contactEmail: venueForm.contactEmail,
        contactPhone: venueForm.contactPhone
      };

      if (editingVenue) {
        // Update existing venue
        await axios.put(
          `${API_BASE}/venues/${editingVenue.id}`,
          venueData,
          { headers: { Authorization: `Bearer ${token}` } }
        );
        setMessage('Venue updated successfully!');
      } else {
        // Create new venue
        await axios.post(
          `${API_BASE}/venues`,
          venueData,
          { headers: { Authorization: `Bearer ${token}` } }
        );
        setMessage('Venue created successfully!');
      }

      setShowVenueForm(false);
      setEditingVenue(null);
      setVenueForm(emptyVenueForm);
      
      // Refresh venues list
      if (canManageVenues) {
        fetchVenues();
      } else {
        fetchUserVenues();
      }
    } catch (error) {
      console.error('Error saving venue:', error);
      setError(error.response?.data?.message || 'Failed to save venue');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteVenue = async (venueId) => {
    if (!window.confirm('Are you sure you want to delete this venue?')) {
      return;
    }

    try {
      setError("");
      setMessage("");
      
      await axios.delete(
        `${API_BASE}/venues/${venueId}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      
      setMessage('Venue deleted successfully!');
      
      // Refresh venues list
      if (canManageVenues) {
        fetchVenues();
      } else {
        fetchUserVenues();
      }
    } catch (error) {
      console.error('Error deleting venue:', error);
      setError(error.response?.data?.message || 'Failed to delete venue');
    }
  };

  const assignVenueToEvent = async () => {
    if (!selectedEvent || !selectedVenue) {
      setError('Please select both an event and a venue');
      return;
    }

    // Frontend validation for role-based access
    if (isOrganizer) {
      setError('Note: Venue assignment is currently restricted to Administrators only. Please contact an Admin to assign venues to events.');
      return;
    }

    try {
      setLoading(true);
      setError('');
      setMessage('');

      // Check venue availability
      const availabilityResponse = await axios.get(
        `${API_BASE}/venues/${selectedVenue}/availability?startDateTime=${selectedEvent.startDateTime}&endDateTime=${selectedEvent.endDateTime}`,
        {
          headers: { Authorization: `Bearer ${token}` }
        }
      );

      if (!availabilityResponse.data.available) {
        setError('Venue is not available during the event time period. Please choose a different venue or time.');
        return;
      }

      // Assign venue to event
      await axios.post(
        `${API_BASE}/events/${selectedEvent.id}/venues/${selectedVenue}`,
        {},
        {
          headers: { Authorization: `Bearer ${token}` }
        }
      );

      setMessage('Venue successfully assigned to event!');
      setSelectedEvent(null);
      setSelectedVenue(null);
      
      // Refresh data
      fetchEvents();
      fetchVenues();
      fetchVenueEvents(); // Refresh venue events to show the new assignment
    } catch (error) {
      console.error('Error assigning venue:', error);
      
      // Handle specific error messages
      if (error.response?.data?.message) {
        const errorMessage = error.response.data.message;
        
        if (errorMessage.includes('CANCELLED')) {
          setError('Cannot assign venue to cancelled events. Please select an active event.');
        } else if (errorMessage.includes('DRAFT or ACTIVE')) {
          setError('Only DRAFT or ACTIVE events can have venues assigned.');
        } else if (errorMessage.includes('not available')) {
          setError('Venue is not available during the event time period. Please choose a different venue or time.');
        } else {
          setError(errorMessage);
        }
      } else if (error.response?.status === 403) {
        setError('Access denied. Only Administrators can assign venues to events.');
      } else {
        setError('Failed to assign venue to event. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  const [venueEvents, setVenueEvents] = useState({});

  // Fetch events for each venue by checking all events and their venues
  const fetchVenueEvents = async () => {
    try {
      // Get all events first
      const eventsResponse = await axios.get(`${API_BASE}/events`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      
      const allEvents = eventsResponse.data;
      const eventsMap = {};
      
      // For each venue, find events that have this venue assigned
      for (const venue of venues) {
        const venueAssignedEvents = [];
        
        for (const event of allEvents) {
          try {
            // Get venues for this event
            const eventVenuesResponse = await axios.get(`${API_BASE}/events/${event.id}/venues`, {
              headers: { Authorization: `Bearer ${token}` }
            });
            
            const eventVenues = eventVenuesResponse.data;
            
            // Check if this venue is assigned to this event
            if (eventVenues.some(v => v.id === venue.id)) {
              venueAssignedEvents.push(event);
            }
          } catch (error) {
            console.error(`Error checking venues for event ${event.id}:`, error);
          }
        }
        
        eventsMap[venue.id] = venueAssignedEvents;
      }
      
      setVenueEvents(eventsMap);
    } catch (error) {
      console.error('Error fetching venue events:', error);
    }
  };

  // Load events for all venues when venues are fetched
  useEffect(() => {
    if (venues.length > 0) {
      fetchVenueEvents();
    }
  }, [venues]);

  const activeVenues = venues;

  return (
    <div className="venues-page">
      <div className="content-card">
        {/* Role Preview Section */}
        {realIsAdmin && (
          <section className="venue-section">
            <h3 className="venue-section-title">Preview Page As</h3>
            <div className="venue-form">
              <div className="venue-form-grid">
                <div className="venue-form-group">
                  <label className="venue-label" htmlFor="preview-role-select">
                    Role Preview
                  </label>
                  <select
                    id="preview-role-select"
                    className="venue-select"
                    value={previewRole ?? ""}
                    onChange={(e) => {
                      setPreviewRole(e.target.value || null);
                      setShowVenueForm(false);
                    }}
                  >
                    <option value="">My Real Role ({userRole || "unknown"})</option>
                    {PREVIEW_ROLES.map((r) => (
                      <option key={r} value={r}>{r}</option>
                    ))}
                  </select>
                </div>
              </div>
            </div>
          </section>
        )}

        <h1>{canManageVenues ? "Venue Management" : "My Venues"}</h1>
        <p>
          {canManageVenues 
            ? "Create, edit venues and assign them to events" 
            : "Venues for events you are registered for or assigned to"
          }
        </p>

        {message && (
          <div className="success-message">{message}</div>
        )}

        {error && (
          <div className="error-message">{error}</div>
        )}

        {/* Action Buttons */}
        <div style={{ display: "flex", gap: "0.75rem", marginBottom: "1.25rem", flexWrap: "wrap" }}>
          {!showVenueForm && canEdit && (
            <button className="assign-button" onClick={openCreateVenueForm}>
              + Add New Venue
            </button>
          )}
        </div>

        {/* Venue Form */}
        {showVenueForm && (
          <VenueForm
            form={venueForm}
            setForm={setVenueForm}
            submitting={submitting}
            onSubmit={handleVenueFormSubmit}
            error={error}
          />
        )}

        {/* Venue Assignment Section */}
        {canManageVenues && !showVenueForm && (
          <div className="venue-assignment-section">
          <h2>Assign Venue to Event</h2>
          
          {isOrganizer && (
            <div className="role-warning">
              <p><strong>Note:</strong> Venue assignment is currently restricted to Administrators only. Organizers can view venues but cannot assign them to events.</p>
            </div>
          )}
          
          <div className="form-row">
            <div className="form-group">
              <label>Select Event:</label>
              <select 
                value={selectedEvent?.id || ''} 
                onChange={(e) => {
                  const event = events.find(ev => ev.id === parseInt(e.target.value));
                  setSelectedEvent(event);
                }}
              >
                <option value="">Choose an event...</option>
                {events.map(event => (
                  <option key={event.id} value={event.id}>
                    {event.name} - {event.startDateTime} to {event.endDateTime}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Select Venue:</label>
              <select 
                value={selectedVenue || ''} 
                onChange={(e) => setSelectedVenue(parseInt(e.target.value))}
                disabled={!selectedEvent}
              >
                <option value="">Choose a venue...</option>
                {activeVenues.map(venue => (
                  <option key={venue.id} value={venue.id}>
                    {venue.name} - Capacity: {venue.capacity}
                  </option>
                ))}
              </select>
            </div>
          </div>

          {selectedEvent && selectedVenue && (
            <div className="assignment-preview">
              <h3>Assignment Preview</h3>
              <div className="preview-details">
                <p><strong>Event:</strong> {selectedEvent.name}</p>
                <p><strong>Time:</strong> {selectedEvent.startDateTime} - {selectedEvent.endDateTime}</p>
                <p><strong>Venue:</strong> {activeVenues.find(v => v.id === selectedVenue)?.name}</p>
                <p><strong>Capacity:</strong> {activeVenues.find(v => v.id === selectedVenue)?.capacity}</p>
                <p><strong>Address:</strong> {activeVenues.find(v => v.id === selectedVenue)?.address?.street}, {activeVenues.find(v => v.id === selectedVenue)?.address?.city}</p>
              </div>
              <div className="availability-status">
                <p><strong>Status:</strong> Checking venue availability...</p>
              </div>
              <button 
                className="assign-button" 
                onClick={assignVenueToEvent}
                disabled={loading}
              >
                {loading ? 'Assigning...' : 'Assign Venue to Event'}
              </button>
            </div>
          )}
        </div>
        )}

        {/* Venues List Section */}
        <div className="venues-list-section">
          <h2>Active Venues</h2>
          {loading ? (
            <div className="loading">Loading venues...</div>
          ) : activeVenues.length > 0 ? (
            <div className="venues-grid">
              {activeVenues.map(venue => (
                <div key={venue.id} className="venue-card">
                  <h3>{venue.name}</h3>
                  <div className="venue-details">
                    <p><strong>Address:</strong> {venue.address?.street}, {venue.address?.city}</p>
                    <p><strong>Capacity:</strong> {venue.capacity}</p>
                    <p><strong>Contact:</strong> {venue.contactName}</p>
                    <p><strong>Email:</strong> {venue.contactEmail}</p>
                    <p><strong>Phone:</strong> {venue.contactPhone}</p>
                  </div>
                  
                  {/* Assigned Events Section */}
                  <div className="venue-events">
                    <h4>Assigned Events:</h4>
                    {venueEvents[venue.id] && venueEvents[venue.id].length > 0 ? (
                      <div className="assigned-events-list">
                        {venueEvents[venue.id].map(event => (
                          <div key={event.id} className="assigned-event-item">
                            <p><strong>{event.name}</strong></p>
                            <p className="event-time">{event.startDateTime} - {event.endDateTime}</p>
                            <p className="event-status">Status: {event.status}</p>
                          </div>
                        ))}
                      </div>
                    ) : (
                      <p className="no-events">No events assigned</p>
                    )}
                  </div>
                  
                  {canEdit && (
                    <div className="venue-actions">
                      <button 
                        className="btn btn-primary" 
                        onClick={() => startEditingVenue(venue)}
                      >
                        Edit
                      </button>
                      <button 
                        className="btn btn-danger" 
                        onClick={() => handleDeleteVenue(venue.id)}
                      >
                        Delete
                      </button>
                    </div>
                  )}
                </div>
              ))}
            </div>
          ) : (
            <div className="no-venues">
              <p>
                {canManageVenues 
                  ? "No venues found. Create your first venue using the 'Add New Venue' button." 
                  : "You haven't been assigned to any events with venues yet."
                }
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}