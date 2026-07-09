import { useState } from 'react';
import './AppointmentCard.css';

/**
 * @typedef {Object} Appointment
 * @property {string|number} id
 * @property {string} animalName
 * @property {string} nome
 * @property {string} cognome
 * @property {string} visitType
 * @property {string} appointmentHour
 * @property {string} doctorName
 */

function getClientFullName(appointment) {
  return [appointment.nome, appointment.cognome].filter(Boolean).join(' ');
}

/**
 * @param {{
 *   appointment: Appointment,
 *   onEdit?: (appointment: Appointment) => void,
 *   onDelete?: (appointment: Appointment) => void,
 *   onDelayNotification?: (appointment: Appointment) => void
 * }} props
 */
export function AppointmentCard({
  appointment,
  onEdit,
  onDelete,
  onDelayNotification
}) {
  const [isExpanded, setIsExpanded] = useState(false);
  const clientFullName = getClientFullName(appointment);

  function toggleDetails() {
    setIsExpanded((currentValue) => !currentValue);
  }

  function handleActionClick(event, action) {
    event.stopPropagation();
    action?.(appointment);
  }

  return (
    <article className={`appointment-card${isExpanded ? ' appointment-card--expanded' : ''}`}>
      <button
        type="button"
        className="appointment-card__summary"
        aria-expanded={isExpanded}
        onClick={toggleDetails}
      >
        <div className="appointment-card__client">
          <h3 className="appointment-card__title">{clientFullName}</h3>
          <span className="appointment-card__hint">Clicca per vedere i dettagli</span>
        </div>

        <div className="appointment-card__field">
          <span>Orario</span>
          <strong>{appointment.appointmentHour}</strong>
        </div>

        <div className="appointment-card__field">
          <span>Medico</span>
          <strong>{appointment.doctorName}</strong>
        </div>

        <span className="appointment-card__chevron" aria-hidden="true">
          v
        </span>
      </button>

      {isExpanded && (
        <div className="appointment-card__details">
          <div className="appointment-card__field">
            <span>Animale</span>
            <strong>{appointment.animalName}</strong>
          </div>

          <div className="appointment-card__field">
            <span>Cliente</span>
            <strong>{clientFullName}</strong>
          </div>

          <div className="appointment-card__field">
            <span>Tipo visita</span>
            <strong>{appointment.visitType}</strong>
          </div>

          <div className="appointment-card__field">
            <span>Orario</span>
            <strong>{appointment.appointmentHour}</strong>
          </div>

          <div className="appointment-card__field">
            <span>Medico</span>
            <strong>{appointment.doctorName}</strong>
          </div>
        </div>
      )}

      <div className="appointment-card__actions" aria-label="Azioni appuntamento">
        <button
          type="button"
          className="btn btn-outline btn-sm"
          onClick={(event) => handleActionClick(event, onEdit)}
        >
          Modifica
        </button>

        <button
          type="button"
          className="btn btn-danger btn-sm"
          onClick={(event) => handleActionClick(event, onDelete)}
        >
          Cancella
        </button>

        <button
          type="button"
          className="btn btn-secondary btn-sm"
          onClick={(event) => handleActionClick(event, onDelayNotification)}
        >
          Not. Ritardo
        </button>
      </div>
    </article>
  );
}

/**
 * @param {{
 *   appointments: Appointment[],
 *   onEdit?: (appointment: Appointment) => void,
 *   onDelete?: (appointment: Appointment) => void,
 *   onDelayNotification?: (appointment: Appointment) => void
 * }} props
 */
export function AppointmentCardList({
  appointments,
  onEdit,
  onDelete,
  onDelayNotification
}) {
  return (
    <div className="appointment-card-list">
      {appointments.map((appointment, index) => (
        <AppointmentCard
          key={appointment.id ?? index}
          appointment={appointment}
          onEdit={onEdit}
          onDelete={onDelete}
          onDelayNotification={onDelayNotification}
        />
      ))}
    </div>
  );
}
