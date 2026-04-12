import { useMemo, useState } from "react";
import { Link, useLocation } from "react-router-dom";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export default function ResetPasswordPage() {
  const location = useLocation();
  const token = useMemo(() => new URLSearchParams(location.search).get("token") || "", [location.search]);

  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [message, setMessage] = useState("");

  async function handleSubmit(e) {
    e.preventDefault();
    setMessage("");

    if (newPassword !== confirmPassword) {
      setMessage("Passwords do not match.");
      return;
    }

    try {
      const res = await fetch(`${API_BASE_URL}/auth/reset-password`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          token,
          newPassword
        })
      });

      const text = await res.text();
      if (!res.ok) throw new Error(text || "Reset failed");

      setMessage(text);
      setNewPassword("");
      setConfirmPassword("");
    } catch (err) {
      setMessage(err instanceof Error ? err.message : "Something went wrong");
    }
  }

  return (
    <div className="login-page">
      <div className="login-card signup-single">
        <section className="login-form-section signup-full">
          <h2>Reset Password</h2>

          <form onSubmit={handleSubmit} className="login-form">
            <input
              type="password"
              placeholder="New Password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              required
            />

            <input
              type="password"
              placeholder="Confirm New Password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
            />

            <button type="submit">Reset Password</button>
          </form>

          {message && <p className="form-message">{message}</p>}

          <p className="signup-text">
            <Link to="/">Back to Login</Link>
          </p>
        </section>
      </div>
    </div>
  );
}