import "./VenueTable.css";

export default function VenueTable({ venues, onEdit, onDelete, onStatusChange, loading, userRole }) {
  const handleStatusClick = async (venueId, newStatus) => {
    onStatusChange(venueId, newStatus);
  };

  const getRoleDisplayName = (role) => {
    switch (role) {
      case 'ADMIN': return 'Administrator';
      case 'ORGANIZER': return 'Event Organizer';
      case 'STAFF': return 'Staff Member';
      case 'PARTICIPANT': return 'Participant';
      default: return 'User';
    }
  };

  return (
    <div className="venue-table-container">
      {loading ? (
        <div className="loading">Loading venues...</div>
      ) : (
        <table className="venue-table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Address</th>
              <th>Capacity</th>
              <th>Contact Name</th>
              <th>Contact Email</th>
              <th>Contact Phone</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {venues.map((venue, index) => (
              <tr key={venue.id} className={venue.status === 'INACTIVE' ? 'inactive-row' : ''}>
                <td>{venue.name}</td>
                <td>
                  {venue.address?.street && <div>{venue.address.street}</div>}
                  {venue.address?.city && <div>{venue.address.city}, {venue.address?.state} {venue.address?.zipCode}</div>}
                  {venue.address?.country && <div>{venue.address.country}</div>}
                </td>
                <td>{venue.capacity}</td>
                <td>{venue.contactName}</td>
                <td>{venue.contactEmail}</td>
                <td>{venue.contactPhone}</td>
                <td>
                  <span className={`status-badge ${venue.status.toLowerCase()}`}>
                    {venue.status}
                  </span>
                </td>
                <td>
                  <div className="action-buttons">
                    {['ADMIN', 'ORGANIZER'].includes(userRole) && (
                      <>
                        <button 
                          className="edit-button" 
                          onClick={() => onEdit(venue)}
                          title="Edit venue"
                        >
                          ✏️️
                        </button>
                        <button 
                          className="status-button"
                          onClick={() => handleStatusClick(venue.id, venue.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE')}
                          title="Toggle venue status"
                        >
                          {venue.status === 'ACTIVE' ? '🔴' : '🟢'}
                        </button>
                      </>
                    )}
                    
                    {['ADMIN'].includes(userRole) && (
                      <button 
                        className="delete-button" 
                        onClick={() => onDelete(venue.id)}
                        title="Delete venue"
                      >
                        🗑️
                      </button>
                    )}
                    
                    {['STAFF'].includes(userRole) && (
                      <span className="staff-badge" title={getRoleDisplayName('STAFF')}>
                        👤 {getRoleDisplayName('STAFF')}
                      </span>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
