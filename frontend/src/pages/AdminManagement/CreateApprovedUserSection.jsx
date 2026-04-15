export default function CreateApprovedUserSection({
  createUserForm,
  onFieldChange,
  onSubmit,
  roleOptions,
}) {
  return (
    <div className="content-card" style={{ marginBottom: "1.5rem" }}>
      <h2>Create Approved User</h2>
      <p>Create a user account that is immediately enabled.</p>

      <form onSubmit={onSubmit}>
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))",
            gap: "0.75rem",
            marginTop: "1rem",
          }}
        >
          <input
            type="text"
            placeholder="Username"
            value={createUserForm.username}
            onChange={(e) => onFieldChange("username", e.target.value)}
            required
          />

          <input
            type="email"
            placeholder="Email"
            value={createUserForm.email}
            onChange={(e) => onFieldChange("email", e.target.value)}
            required
          />

          <input
            type="password"
            placeholder="Password"
            value={createUserForm.password}
            onChange={(e) => onFieldChange("password", e.target.value)}
            required
          />

          <select
            value={createUserForm.role}
            onChange={(e) => onFieldChange("role", e.target.value)}
          >
            {roleOptions.map((role) => (
              <option key={role} value={role}>
                {role}
              </option>
            ))}
          </select>
        </div>

        <div style={{ marginTop: "1rem" }}>
          <button type="submit" className="table-action-btn">
            Create User
          </button>
        </div>
      </form>
    </div>
  );
}