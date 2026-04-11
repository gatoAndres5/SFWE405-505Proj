import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import reactLogo from "../assets/react.svg";
import viteLogo from "../assets/vite.svg";
import heroImg from "../assets/hero.png";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export default function LoginPage({ onLoginSuccess }) {
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleLogin(e) {
    e.preventDefault();
    setMessage("");
    setLoading(true);

    try {
      const res = await fetch(`${API_BASE_URL}/auth/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ username, password }),
      });

      const data = await res.json().catch(() => null);

      if (!res.ok) {
        throw new Error(data?.message || "Login failed");
      }

      onLoginSuccess(data.token);
      navigate("/dashboard");
    } catch (err) {
      if (err instanceof Error) {
        setMessage(err.message);
      } else {
        setMessage("Something went wrong");
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="login-page">
      <div className="login-card">
        <section className="login-hero">
          <div className="hero">
            <img src={heroImg} className="base" width="170" height="179" alt="" />
            <img src={reactLogo} className="framework" alt="React logo" />
            <img src={viteLogo} className="vite" alt="Vite logo" />
          </div>

          <div className="hero-text">
            <h1>Event Planning System</h1>
            <p>
              Sign in to manage events, venues, participants, vendors, bookings,
              and registrations.
            </p>
          </div>
        </section>

        <section className="login-form-section">
          <h2>Login</h2>

          <form onSubmit={handleLogin} className="login-form">
            <input
              type="text"
              placeholder="Username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />

            <input
              type="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />

            <button type="submit" disabled={loading}>
              {loading ? "Logging in..." : "Login"}
            </button>
          </form>

          {message && <p className="form-message">{message}</p>}

          <p className="signup-text">
            Don&apos;t have an account? <Link to="/signup">Sign up</Link>
          </p>
          <p className="signup-text">
            <Link to="/forgot-password">Forgot password?</Link>
          </p>
        </section>
      </div>
    </div>
  );
}