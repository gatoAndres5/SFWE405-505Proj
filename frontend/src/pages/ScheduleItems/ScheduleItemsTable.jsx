import "./ScheduleItems.css";

function formatDateTime(dt) {
  if (!dt) return "—";
  return new Date(dt).toLocaleString();
}

export default function ScheduleItemsTable({ loading, items, onEdit, onDelete }) {
  return (
    <section className="schedule-section">
      <h2 className="schedule-section-title">All Schedule Items</h2>
      <p className="schedule-section-subtitle">
        View, edit, or remove items from the schedule.
      </p>

      {loading ? (
        <p className="schedule-empty">Loading...</p>
      ) : (
        <div className="schedule-table-wrapper">
          <table className="schedule-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Type</th>
                <th>Description</th>
                <th>Start</th>
                <th>End</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {items.length === 0 ? (
                <tr>
                  <td colSpan="7" className="schedule-empty">
                    No schedule items found.
                  </td>
                </tr>
              ) : (
                items.map((item) => (
                  <tr key={item.id}>
                    <td>{item.id}</td>
                    <td>{item.title}</td>
                    <td>
                      <span className="schedule-badge">{item.type}</span>
                    </td>
                    <td>{item.description}</td>
                    <td>{formatDateTime(item.startDateTime)}</td>
                    <td>{formatDateTime(item.endDateTime)}</td>
                    <td>
                      <div className="schedule-inline-actions">
                        <button
                          className="schedule-btn schedule-btn-primary"
                          onClick={() => onEdit(item)}
                        >
                          Edit
                        </button>
                        <button
                          className="schedule-btn schedule-btn-danger"
                          onClick={() => onDelete(item.id)}
                        >
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}
