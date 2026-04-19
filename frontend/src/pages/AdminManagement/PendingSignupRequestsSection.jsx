import "./AdminManagement.css";
export default function PendingSignupRequestsSection({
  loading,
  pendingUsers,
  onApprove,
}) {
  return (
    <section className="admin-section">
      <h2 className="admin-section-title">Pending Signup Requests</h2>
      <p className="admin-section-subtitle">
        Review and approve new account requests.
      </p>

      {loading ? (
        <p className="admin-muted">Loading...</p>
      ) : (
        <div className="admin-table-wrapper">
          <table className="admin-table">
            <thead>
              <tr>
                <th>Username</th>
                <th>Email</th>
                <th>Enabled</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {pendingUsers.length === 0 ? (
                <tr>
                  <td colSpan="4" className="admin-empty">
                    No pending users
                  </td>
                </tr>
              ) : (
                pendingUsers.map((user) => (
                  <tr key={user.id}>
                    <td>{user.username}</td>

                    <td>{user.email}</td>

                    <td>
                      <span
                        className={`admin-badge ${
                          user.enabled ? "enabled" : "disabled"
                        }`}
                      >
                        {user.enabled ? "Enabled" : "Disabled"}
                      </span>
                    </td>

                    <td>
                      <div className="admin-inline-actions">
                        <button
                          className="admin-btn admin-btn-success"
                          type="button"
                          onClick={() => onApprove(user.id)}
                        >
                          Approve
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