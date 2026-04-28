import { useState } from "react";

export default function VenueForm({ form, setForm, submitting, onSubmit, error }) {
  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const handleAddressChange = (field, value) => {
    setForm(prev => ({
      ...prev,
      address: {
        ...prev.address,
        [field]: value
      }
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(e);
  };

  return (
    <form className="venue-form" onSubmit={handleSubmit}>
      <h3>{form.id ? 'Edit Venue' : 'Add New Venue'}</h3>
      
      {error && (
        <div className="error-message">{error}</div>
      )}

      <div className="form-group">
        <label>Venue Name:</label>
        <input
          type="text"
          name="name"
          value={form.name}
          onChange={handleChange}
          required
        />
      </div>

      <div className="form-group">
        <label>Address:</label>
        <div className="address-fields">
          <input
            type="text"
            name="street"
            placeholder="Street"
            value={form.address?.street || ''}
            onChange={(e) => handleAddressChange('street', e.target.value)}
            required
          />
          <input
            type="text"
            name="city"
            placeholder="City"
            value={form.address?.city || ''}
            onChange={(e) => handleAddressChange('city', e.target.value)}
            required
          />
          <input
            type="text"
            name="state"
            placeholder="State"
            value={form.address?.state || ''}
            onChange={(e) => handleAddressChange('state', e.target.value)}
            required
          />
          <input
            type="text"
            name="zipCode"
            placeholder="ZIP Code"
            value={form.address?.zipCode || ''}
            onChange={(e) => handleAddressChange('zipCode', e.target.value)}
            required
          />
          <input
            type="text"
            name="country"
            placeholder="Country"
            value={form.address?.country || ''}
            onChange={(e) => handleAddressChange('country', e.target.value)}
            required
          />
        </div>
      </div>

      <div className="form-group">
        <label>Capacity:</label>
        <input
          type="number"
          name="capacity"
          value={form.capacity}
          onChange={handleChange}
          min="1"
          required
        />
      </div>

      <div className="form-group">
        <label>Contact Name:</label>
        <input
          type="text"
          name="contactName"
          value={form.contactName}
          onChange={handleChange}
          required
        />
      </div>

      <div className="form-group">
        <label>Contact Email:</label>
        <input
          type="email"
          name="contactEmail"
          value={form.contactEmail}
          onChange={handleChange}
          required
        />
      </div>

      <div className="form-group">
        <label>Contact Phone:</label>
        <input
          type="tel"
          name="contactPhone"
          value={form.contactPhone}
          onChange={handleChange}
          required
        />
      </div>

      <button type="submit" disabled={submitting}>
        {submitting ? 'Saving...' : (form.id ? 'Update Venue' : 'Create Venue')}
      </button>
    </form>
  );
}
