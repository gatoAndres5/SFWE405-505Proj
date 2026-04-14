export default function ExistingUsersSection({
  loading,
  approvedUsers,
  roleOptions,
  onRoleChange,
}) {
  return (
    <div className="content-card" style={{ marginTop: "1.5rem" }}>
      <h2>Existing Users</h2>
      <p>Manage roles and permissions.</p>

      {loading ? (
        <p>Loading...</p>
      ) : (
        <div className="table-wrapper">
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
                  <td colSpan="5">No users found</td>
                </tr>
              ) : (
                approvedUsers.map((user) => (
                  <tr key={user.id}>
                    <td>{user.username}</td>
                    <td>{user.email}</td>
                    <td>{user.role}</td>
                    <td>{user.enabled ? "Yes" : "No"}</td>
                    <td>
                      <select
                        value={user.role}
                        onChange={(e) => onRoleChange(user.id, e.target.value)}
                      >
                        {roleOptions.map((r) => (
                          <option key={r} value={r}>
                            {r}
                          </option>
                        ))}
                      </select>
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