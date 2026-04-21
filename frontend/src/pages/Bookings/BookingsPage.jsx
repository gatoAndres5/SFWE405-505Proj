import { useState, useEffect } from "react";
import axios from "axios";

export default function BookingPage() {
  const [bookings, setBookings] = useState([]);

  const [form, setForm] = useState({
    eventId: "",
    vendorId: "",
    serviceDescription: "",
    startDateTime: "",
    endDateTime: "",
  });

  useEffect(() => {
    fetchBookings();
  }, []);

  const fetchBookings = async () => {
    const res = await axios.get("http://localhost:8080/bookings");
    setBookings(res.data);
  };

  const createBooking = async () => {
    await axios.post("http://localhost:8080/bookings", null, {
      params: form,
    });
    fetchBookings();
  };

  const cancelBooking = async (id) => {
    await axios.put(`http://localhost:8080/bookings/${id}/cancel`);
    fetchBookings();
  };

  const confirmBooking = async (id) => {
    await axios.put(`http://localhost:8080/bookings/${id}/confirm`);
    fetchBookings();
  };

  const completeBooking = async (id) => {
    await axios.put(`http://localhost:8080/bookings/${id}/complete`);
    fetchBookings();
  };

  return (
    <div>
      <h1>Bookings</h1>
      <h2>Create Booking</h2>
      <input placeholder="Event ID" onChange={e => setForm({...form, eventId: e.target.value})} />
      <input placeholder="Vendor ID" onChange={e => setForm({...form, vendorId: e.target.value})} />
      <input placeholder="Description" onChange={e => setForm({...form, serviceDescription: e.target.value })} />
      <input type="datetime-local" onChange={e => setForm({...form, startDateTime: e.target.value})} />
      <input type="datetime-local" onChange={e => setForm({...form, endDateTime: e.target.value})} />
      <button onClick={createBooking}>Create</button>

      <h2>All Bookings</h2>
      {bookings.map((b) => (
        <div key={b.bookingId}>
          <p>{b.serviceDescription}</p>
          <button onClick={() => cancelBooking(b.bookingId)}>Cancel</button>
          <button onClick={() => confirmBooking(b.bookingId)}>Confirm</button>
          <button onClick={() => completeBooking(b.bookingId)}>Complete</button>
        </div>
      ))}
    </div>
  );
}