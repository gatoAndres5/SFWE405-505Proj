import { useEffect, useState } from "react";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const roleOptions = ["PARTICIPANT", "ORGANIZER", "STAFF", "ADMIN"];

export default function AdminManagementPage() {
  const [pendingUsers, setPendingUsers] = useState([]);
  const [approvedUsers, setApprovedUsers] = useState([]);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(true);

  const token = localStorage.getItem("token");

  // 🔄 Fetch users from backend
  async function fetchUsers() {
    try {
      setLoading(true);
      setMessage("");

      const [pendingRes, allRes] = await Promise.all([
        fetch(`${API_BASE_URL}/users/pending`, {
          headers: { Authorization: `Bearer ${token}` },
        }),
        fetch(`${API_BASE_URL}/users`, {
          headers: { Authorization: `Bearer ${token}` },
        }),
      ]);

      const pendingData = await pendingRes.json().catch(() => []);
      const allData = await allRes.json().catch(() => []);

      if (!pendingRes.ok) throw new Error("Failed to load pending users");
      if (!allRes.ok) throw new Error("Failed to load users");

      setPendingUsers(pendingData);
      setApprovedUsers(allData);
    } catch (err) {
      setMessage(err instanceof Error ? err.message : "Error loading users");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    fetchUsers();
  }, []);

  // ✅ Approve user
  async function handleApprove(userId) {
    try {
      const res = await fetch(`${API_BASE_URL}/users/${userId}/approve`, {
        method: "PATCH",
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!res.ok) throw new Error("Failed to approve user");

      setMessage("User approved");
      fetchUsers();
    } catch (err) {
      setMessage(err instanceof Error ? err.message : "Error approving user");
    }
  }

  // 🔄 Change role
  async function handleRoleChange(userId, role) {
    try {
      const res = await fetch(`${API_BASE_URL}/users/${userId}/role`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ role }),
      });

      if (!res.ok) throw new Error("Failed to change role");

      setMessage("Role updated");
      fetchUsers();
    } catch (err) {
      setMessage(err instanceof Error ? err.message : "Error updating role");
    }
  }

  return (
    <div className="admin-management-page">
      {message && <p className="form-message">{message}</p>}

      {/* 🔴 Pending Users */}
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
                          onClick={() => handleApprove(user.id)}
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

      {/* 🟢 Approved Users */}
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
                          onChange={(e) =>
                            handleRoleChange(user.id, e.target.value)
                          }
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
    </div>
  );
}