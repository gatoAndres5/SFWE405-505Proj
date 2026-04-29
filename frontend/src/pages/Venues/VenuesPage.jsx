import { useEffect, useState } from "react";
import axios from "axios";
import "./Venues.css";

const API_BASE = "http://localhost:8080";

export default function VenuesPage() {
  const [venues, setVenues] = useState([]);
  const [events, setEvents] = useState([]);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [selectedVenue, setSelectedVenue] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

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
      // For participants, fetch venues from events they're registered/assigned to
      const eventsResponse = await axios.get(`${API_BASE}/events/my-events`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      
      const userEvents = eventsResponse.data;
      const userVenueIds = new Set();
      
      userEvents.forEach(event => {
        if (event.venues) {
          event.venues.forEach(venue => {
            userVenueIds.add(venue.venueId);
          });
        }
      });
      
      // Fetch venue details for the venues user has access to
      if (userVenueIds.size > 0) {
        const venuesResponse = await axios.get(`${API_BASE}/venues/list`, {
          headers: { Authorization: `Bearer ${token}` }
        });
        
        const userVenues = venuesResponse.data.filter(venue => 
          userVenueIds.has(venue.venueId)
        );
        
        setVenues(userVenues);
      } else {
        setVenues([]);
      }
    } catch (error) {
      setError('Failed to fetch your venues');
      console.error('Error fetching user venues:', error);
    } finally {
      setLoading(false);
    }
  };

  const assignVenueToEvent = async () => {
    if (!selectedEvent || !selectedVenue) {
      setError('Please select both an event and a venue');
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

      if (!availabilityResponse.data) {
        setError('Venue is not available during the event time period');
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
    } catch (error) {
      setError('Failed to assign venue to event');
      console.error('Error assigning venue:', error);
    } finally {
      setLoading(false);
    }
  };

  const activeVenues = venues;

  return (
    <div className="venues-page">
      <div className="content-card">
        <h1>{canManageVenues ? "Venue Management" : "My Venues"}</h1>
        <p>
          {canManageVenues 
            ? "Assign venues to events and manage venue availability" 
            : "Venues for events you are registered for or assigned to"
          }
        </p>

        {message && (
          <div className="success-message">{message}</div>
        )}

        {error && (
          <div className="error-message">{error}</div>
        )}

        {canManageVenues && (
          <div className="venue-assignment-section">
          <h2>Assign Venue to Event</h2>
          
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
                </div>
              ))}
            </div>
          ) : (
            <div className="no-venues">
              <p>
                {canManageVenues 
                  ? "No venues found. Assign venues to events to see them here." 
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