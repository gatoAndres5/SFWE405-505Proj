import { useEffect, useState } from "react";
import CreateApprovedUserSection from "./CreateApprovedUserSection";
import PendingSignupRequestsSection from "./PendingSignupRequestsSection";
import ExistingUsersSection from "./ExistingUsersSection";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const roleOptions = ["PARTICIPANT", "ORGANIZER", "STAFF", "ADMIN"];

export default function AdminManagementPage() {
  const [pendingUsers, setPendingUsers] = useState([]);
  const [approvedUsers, setApprovedUsers] = useState([]);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(true);

  const [createUserForm, setCreateUserForm] = useState({
    username: "",
    email: "",
    password: "",
    role: "PARTICIPANT",
  });

  const token = localStorage.getItem("token");

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

  async function handleCreateUser(e) {
    e.preventDefault();

    try {
      setMessage("");

      const res = await fetch(`${API_BASE_URL}/users`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(createUserForm),
      });

      const data = await res.json().catch(() => null);

      if (!res.ok) {
        throw new Error(data?.message || "Failed to create user");
      }

      setMessage("Approved user created successfully");
      setCreateUserForm({
        username: "",
        email: "",
        password: "",
        role: "PARTICIPANT",
      });
      fetchUsers();
    } catch (err) {
      setMessage(err instanceof Error ? err.message : "Error creating user");
    }
  }

  function handleCreateUserFieldChange(field, value) {
    setCreateUserForm((prev) => ({
      ...prev,
      [field]: value,
    }));
  }

  return (
    <div className="admin-management-page">
      {message && <p className="form-message">{message}</p>}

      <CreateApprovedUserSection
        createUserForm={createUserForm}
        onFieldChange={handleCreateUserFieldChange}
        onSubmit={handleCreateUser}
        roleOptions={roleOptions}
      />

      <PendingSignupRequestsSection
        loading={loading}
        pendingUsers={pendingUsers}
        onApprove={handleApprove}
      />

      <ExistingUsersSection
        loading={loading}
        approvedUsers={approvedUsers}
        roleOptions={roleOptions}
        onRoleChange={handleRoleChange}
      />
    </div>
  );
}