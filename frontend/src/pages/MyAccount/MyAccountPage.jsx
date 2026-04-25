import { useEffect, useState } from "react";
import "./MyAccountPage.css";

const API_BASE_URL = "http://localhost:8080";

export default function MyAccountPage() {
  const [account, setAccount] = useState(null);

  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");

  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");

  const [loading, setLoading] = useState(true);
  const [savingAccount, setSavingAccount] = useState(false);
  const [changingPassword, setChangingPassword] = useState(false);

  const [accountMessage, setAccountMessage] = useState("");
  const [passwordMessage, setPasswordMessage] = useState("");
  const [error, setError] = useState("");

  const token = localStorage.getItem("token");

  useEffect(() => {
    async function fetchAccount() {
      try {
        const response = await fetch(`${API_BASE_URL}/users/me`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          throw new Error("Failed to load account information.");
        }

        const data = await response.json();

        setAccount(data);
        setUsername(data.username || "");
        setEmail(data.email || "");
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }

    fetchAccount();
  }, [token]);

  async function handleUpdateAccount(e) {
    e.preventDefault();
    setSavingAccount(true);
    setAccountMessage("");
    setError("");

    try {
      const response = await fetch(`${API_BASE_URL}/users/me`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          username,
          email,
        }),
      });

      if (!response.ok) {
        const message = await response.text();
        throw new Error(message || "Failed to update account.");
      }

      const updatedUser = await response.json();
      setAccount(updatedUser);
      setUsername(updatedUser.username || "");
      setEmail(updatedUser.email || "");
      setAccountMessage("Account information updated successfully.");
      localStorage.removeItem("token");
      alert("Account updated successfully. Please log in again.");
      window.location.href = "/login";
    } catch (err) {
      setError(err.message);
    } finally {
      setSavingAccount(false);
    }
  }

  async function handleChangePassword(e) {
    e.preventDefault();
    setChangingPassword(true);
    setPasswordMessage("");
    setError("");

    try {
      const response = await fetch(`${API_BASE_URL}/users/me/password`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          currentPassword,
          newPassword,
        }),
      });

      if (!response.ok) {
        const data = await response.json();
        throw new Error(data.message || "Failed to change password.");
      }

      setCurrentPassword("");
      setNewPassword("");
      setPasswordMessage("Password changed successfully.");
    } catch (err) {
      setError(err.message);
    } finally {
      setChangingPassword(false);
    }
  }

  if (loading) {
    return <p>Loading account...</p>;
  }

  return (
  <div className="my-account-page">

    {error && (
      <div className="my-account-alert error">
        {error}
      </div>
    )}

    {accountMessage && (
      <div className="my-account-alert success">
        {accountMessage}
      </div>
    )}

    {passwordMessage && (
      <div className="my-account-alert success">
        {passwordMessage}
      </div>
    )}
    <div className="my-account-card-grid">

      {/* LEFT: ACCOUNT INFO */}
      <section className="my-account-card">
        <h3>Account Information</h3>

        {account && (
          <div className="account-summary">
            <div className="account-summary-item">
              <span className="account-summary-label">Username</span>
              <span className="account-summary-value">{account.username}</span>
            </div>

            <div className="account-summary-item">
              <span className="account-summary-label">Email</span>
              <span className="account-summary-value">{account.email}</span>
            </div>

            <div className="account-summary-item">
              <span className="account-summary-label">Role</span>
              <span className="account-summary-value">{account.role}</span>
            </div>

            <div className="account-summary-item">
              <span className="account-summary-label">Status</span>
              <span className="account-summary-value">
                {account.enabled ? "Enabled" : "Disabled"}
              </span>
            </div>
          </div>
        )}

        <form className="my-account-form" onSubmit={handleUpdateAccount}>
          <div className="my-account-form-group">
            <label>Username</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>

          <div className="my-account-form-group">
            <label>Email</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <button
            className="my-account-button"
            type="submit"
            disabled={savingAccount}
          >
            {savingAccount ? "Saving..." : "Save Account Changes"}
          </button>
        </form>
      </section>

      {/* RIGHT: PASSWORD */}
      <section className="my-account-card">
        <h3>Change Password</h3>

        <form className="my-account-form" onSubmit={handleChangePassword}>
          <div className="my-account-form-group">
            <label>Current Password</label>
            <input
              type="password"
              value={currentPassword}
              onChange={(e) => setCurrentPassword(e.target.value)}
              required
            />
          </div>

          <div className="my-account-form-group">
            <label>New Password</label>
            <input
              type="password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              required
            />
          </div>

          <button
            className="my-account-button"
            type="submit"
            disabled={changingPassword}
          >
            {changingPassword ? "Changing..." : "Change Password"}
          </button>
        </form>
      </section>

    </div>
  </div>
);
}