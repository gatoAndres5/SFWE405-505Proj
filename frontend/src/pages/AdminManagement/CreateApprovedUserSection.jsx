import "./AdminManagement.css";
export default function CreateApprovedUserSection({
  createUserForm,
  onFieldChange,
  onSubmit,
  roleOptions,
}) {
  return (
    <section className="admin-section">
      <h2 className="admin-section-title">Create Approved User</h2>
      <p className="admin-section-subtitle">
        Create a user account that is immediately enabled.
      </p>

      <form className="admin-form" onSubmit={onSubmit}>
        <div className="admin-form-grid">
          <div className="admin-form-group">
            <label className="admin-label">Username</label>
            <input
              className="admin-input"
              type="text"
              placeholder="Username"
              value={createUserForm.username}
              onChange={(e) => onFieldChange("username", e.target.value)}
              required
            />
          </div>

          <div className="admin-form-group">
            <label className="admin-label">Email</label>
            <input
              className="admin-input"
              type="email"
              placeholder="Email"
              value={createUserForm.email}
              onChange={(e) => onFieldChange("email", e.target.value)}
              required
            />
          </div>

          <div className="admin-form-group">
            <label className="admin-label">Password</label>
            <input
              className="admin-input"
              type="password"
              placeholder="Password"
              value={createUserForm.password}
              onChange={(e) => onFieldChange("password", e.target.value)}
              required
            />
          </div>

          <div className="admin-form-group">
            <label className="admin-label">Role</label>
            <select
              className="admin-select"
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
        </div>

        <div className="admin-actions-row">
          <button type="submit" className="admin-btn admin-btn-primary">
            Create User
          </button>
        </div>
      </form>
    </section>
  );
}