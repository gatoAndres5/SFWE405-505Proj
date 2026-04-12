import { useState } from "react";
import { Link } from "react-router-dom";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState("");

  async function handleSubmit(e) {
    e.preventDefault();
    setMessage("");

    try {
      const res = await fetch(`${API_BASE_URL}/auth/forgot-password`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ email })
      });

      const text = await res.text();
      if (!res.ok) throw new Error(text || "Request failed");

      setMessage(text);
      setEmail("");
    } catch (err) {
      setMessage(err instanceof Error ? err.message : "Something went wrong");
    }
  }

  return (
    <div className="login-page">
      <div className="login-card signup-single">
        <section className="login-form-section signup-full">
          <h2>Forgot Password</h2>

          <form onSubmit={handleSubmit} className="login-form">
            <input
              type="email"
              placeholder="Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
            <button type="submit">Send Reset Link</button>
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