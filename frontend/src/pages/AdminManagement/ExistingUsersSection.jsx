import "./AdminManagement.css";
export default function ExistingUsersSection({
  loading,
  approvedUsers,
  roleOptions,
  onRoleChange,
}) {
  function getRoleBadgeClass(role) {
    switch (role) {
      case "ADMIN":
        return "admin-role";
      case "ORGANIZER":
        return "organizer-role";
      case "STAFF":
        return "staff-role";
      case "PARTICIPANT":
        return "participant-role";
      default:
        return "";
    }
  }

  return (
    <section className="admin-section">
      <h2 className="admin-section-title">Existing Users</h2>
      <p className="admin-section-subtitle">Manage roles and permissions.</p>

      {loading ? (
        <p className="admin-muted">Loading...</p>
      ) : (
        <div className="admin-table-wrapper">
          <table className="admin-table">
            <thead>
              <tr>
                <th>Username</th>
                <th>Email</th>
                <th>Role</th>
                <th>Enabled</th>
                <th>Change Role</th>
              </tr>
            </thead>

            <tbody>
              {approvedUsers.length === 0 ? (
                <tr>
                  <td colSpan="5" className="admin-empty">
                    No users found
                  </td>
                </tr>
              ) : (
                approvedUsers.map((user) => (
                  <tr key={user.id}>
                    <td>{user.username}</td>

                    <td>{user.email}</td>

                    <td>
                      <span
                        className={`admin-badge ${getRoleBadgeClass(user.role)}`}
                      >
                        {user.role}
                      </span>
                    </td>

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
                        <select
                          className="admin-select"
                          value={user.role}
                          onChange={(e) => onRoleChange(user.id, e.target.value)}
                        >
                          {roleOptions.map((r) => (
                            <option key={r} value={r}>
                              {r}
                            </option>
                          ))}
                        </select>
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