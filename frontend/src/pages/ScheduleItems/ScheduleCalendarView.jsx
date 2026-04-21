import { useState } from "react";
import { Calendar, dateFnsLocalizer, Views } from "react-big-calendar";
import { format, parse, startOfWeek, getDay } from "date-fns";
import { enUS } from "date-fns/locale/en-US";
import "react-big-calendar/lib/css/react-big-calendar.css";

const localizer = dateFnsLocalizer({
  format,
  parse,
  startOfWeek: () => startOfWeek(new Date(), { weekStartsOn: 0 }),
  getDay,
  locales: { "en-US": enUS },
});

const TYPE_COLORS = {
  Workshop:   "#2563eb",
  Keynote:    "#7c3aed",
  Panel:      "#0891b2",
  Break:      "#64748b",
  Networking: "#16a34a",
  Other:      "#d97706",
};

function eventStyleGetter(event) {
  const color = TYPE_COLORS[event.type] || "#2563eb";
  return {
    style: {
      backgroundColor: color,
      border: "none",
      borderRadius: "6px",
      color: "#fff",
      fontSize: "0.82rem",
      padding: "2px 6px",
    },
  };
}

export default function ScheduleCalendarView({ items }) {
  const [currentView, setCurrentView] = useState(Views.MONTH);
  const [currentDate, setCurrentDate] = useState(new Date());

  const events = items
    .filter((item) => item.startDateTime && item.endDateTime)
    .map((item) => ({
      id: item.id,
      title: item.title,
      start: new Date(item.startDateTime),
      end: new Date(item.endDateTime),
      type: item.type,
      description: item.description,
    }));

  return (
    <section className="schedule-section">
      <h2 className="schedule-section-title">Calendar View</h2>
      <p className="schedule-section-subtitle">
        All schedule items displayed on a calendar. Switch views using the buttons top-right.
      </p>
      <div style={{ height: 600 }}>
        <Calendar
          localizer={localizer}
          events={events}
          startAccessor="start"
          endAccessor="end"
          eventPropGetter={eventStyleGetter}
          style={{ background: "#0b1730", borderRadius: 12, padding: "0.5rem" }}
          views={[Views.MONTH, Views.WEEK, Views.DAY, Views.AGENDA]}
          view={currentView}
          onView={setCurrentView}
          date={currentDate}
          onNavigate={setCurrentDate}
          tooltipAccessor={(e) => `${e.title}${e.description ? " — " + e.description : ""}`}
        />
      </div>
    </section>
  );
}
