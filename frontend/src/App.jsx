import { Navigate, Route, Routes } from "react-router-dom";
import { useState } from "react";
import AppLayout from "./components/layout/AppLayout";
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";
import Dashboard from "./pages/Dashboard";
import EventsPage from "./pages/Events/EventsPage";
import VenuesPage from "./pages/Venues/VenuesPage";
import ParticipantsPage from "./pages/Participants/ParticipantsPage";
import VendorsPage from "./pages/Vendors/VendorsPage";
import BookingsPage from "./pages/Bookings/BookingsPage";
import RegistrationsPage from "./pages/Registrations/RegistrationsPage";
import AdminManagementPage from "./pages/AdminManagementPage";
import ForgotPasswordPage from "./pages/ForgotPasswordPage";
import ResetPasswordPage from "./pages/ResetPasswordPage";
import ScheduleItemsPage from "./pages/ScheduleItems/ScheduleItemsPage";
import "./App.css";

function ProtectedRoute({ isAuthenticated, children }) {
  return isAuthenticated ? children : <Navigate to="/" replace />;
}

export default function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(
    !!localStorage.getItem("token")
  );

  function handleLoginSuccess(token) {
    localStorage.setItem("token", token);
    setIsAuthenticated(true);
  }

  function handleLogout() {
    localStorage.removeItem("token");
    setIsAuthenticated(false);
  }

  return (
    <Routes>
      <Route
        path="/"
        element={<LoginPage onLoginSuccess={handleLoginSuccess} />}
      />
      <Route path="/signup" element={<SignupPage />} />

      <Route
        path="/dashboard"
        element={
          <ProtectedRoute isAuthenticated={isAuthenticated}>
            <AppLayout onLogout={handleLogout}>
              <Dashboard />
            </AppLayout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/events"
        element={
          <ProtectedRoute isAuthenticated={isAuthenticated}>
            <AppLayout onLogout={handleLogout}>
              <EventsPage />
            </AppLayout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/venues"
        element={
          <ProtectedRoute isAuthenticated={isAuthenticated}>
            <AppLayout onLogout={handleLogout}>
              <VenuesPage />
            </AppLayout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/scheduleItems"
        element={
          <ProtectedRoute isAuthenticated={isAuthenticated}>
            <AppLayout onLogout={handleLogout}>
              <ScheduleItemsPage />
            </AppLayout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/participants"
        element={
          <ProtectedRoute isAuthenticated={isAuthenticated}>
            <AppLayout onLogout={handleLogout}>
              <ParticipantsPage />
            </AppLayout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/vendors"
        element={
          <ProtectedRoute isAuthenticated={isAuthenticated}>
            <AppLayout onLogout={handleLogout}>
              <VendorsPage />
            </AppLayout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/bookings"
        element={
          <ProtectedRoute isAuthenticated={isAuthenticated}>
            <AppLayout onLogout={handleLogout}>
              <BookingsPage />
            </AppLayout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/registrations"
        element={
          <ProtectedRoute isAuthenticated={isAuthenticated}>
            <AppLayout onLogout={handleLogout}>
              <RegistrationsPage />
            </AppLayout>
          </ProtectedRoute>
        }
      />

      <Route path="*" element={<Navigate to="/" replace />} />
      <Route path="/signup" element={<SignupPage />} />
      <Route
        path="/admin"
        element={
          <ProtectedRoute isAuthenticated={isAuthenticated}>
            <AppLayout onLogout={handleLogout}>
              <AdminManagementPage />
            </AppLayout>
          </ProtectedRoute>
        }
      />
      <Route path="/forgot-password" element={<ForgotPasswordPage />} />
      <Route path="/reset-password" element={<ResetPasswordPage />} />
    </Routes>
  );
}