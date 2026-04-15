export default function PendingSignupRequestsSection({
  loading,
  pendingUsers,
  onApprove,
}) {
  return (
    <div className="content-card">
      <h2>Pending Signup Requests</h2>
      <p>Review and approve new account requests.</p>

      {loading ? (
        <p>Loading...</p>
      ) : (
        <div className="table-wrapper">
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
                  <td colSpan="4">No pending users</td>
                </tr>
              ) : (
                pendingUsers.map((user) => (
                  <tr key={user.id}>
                    <td>{user.username}</td>
                    <td>{user.email}</td>
                    <td>{user.enabled ? "Yes" : "No"}</td>
                    <td>
                      <button
                        className="table-action-btn"
                        onClick={() => onApprove(user.id)}
                      >
                        Approve
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}