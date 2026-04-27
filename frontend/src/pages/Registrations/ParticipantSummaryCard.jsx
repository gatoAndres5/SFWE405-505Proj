import "./Registrations.css";
export default function ParticipantSummaryCard({ participant }) {
  if (!participant) return null;

  const fullName = `${participant.firstName ?? ""} ${participant.lastName ?? ""}`.trim();

  return (
    <div className="reg-summary-card">
      <h4>{fullName || "Unnamed Participant"}</h4>

      <p>
        <span className="reg-muted">Email:</span>{" "}
        {participant.email ?? "No email available"}
      </p>

      <p>
        <span className="reg-muted">Role:</span>{" "}
        {participant.role ?? "N/A"}
      </p>

      <p>
        <span className="reg-muted">Status:</span>{" "}
        <span
          className={`reg-badge ${
            participant.active ? "confirmed" : "cancelled"
          }`}
        >
          {participant.active ? "Active" : "Inactive"}
        </span>
      </p>
    </div>
  );
}