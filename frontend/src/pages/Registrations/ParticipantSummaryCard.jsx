export default function ParticipantSummaryCard({ participant }) {
  if (!participant) return null;

  return (
    <div
      style={{
        marginBottom: "1rem",
        padding: "0.75rem",
        border: "1px solid rgba(255,255,255,0.15)",
        borderRadius: "8px",
      }}
    >
      <strong>
        {participant.firstName ?? ""} {participant.lastName ?? ""}
      </strong>
      <div>{participant.email ?? "No email available"}</div>
      <div>Role: {participant.role ?? "N/A"}</div>
      <div>Active: {participant.active ? "Yes" : "No"}</div>
    </div>
  );
}