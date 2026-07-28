import { useState } from 'react';
import './AppointmentCard.css';

/**
 * @typedef {Object} Appointment
 * @property {string|number} id
 * @property {string} animalName
 * @property {string} nome
 * @property {string} cognome
 * @property {string} ownerName
 * @property {string} visitType
 * @property {string} appointmentDate
 * @property {string} appointmentHour
 * @property {string} doctorName
 * @property {string} status
 */

function getClientFullName(appointment) {
  return appointment.ownerName || [appointment.nome, appointment.cognome].filter(Boolean).join(' ');
}

function getAppointmentDateTime(appointment) {
  return [appointment.appointmentDate, appointment.appointmentHour].filter(Boolean).join(' ');
}

function getSummaryConfig(appointment, variant) {
  const clientFullName = getClientFullName(appointment);

  if (variant === 'client') {
    return {
      title: appointment.animalName,
      hint: 'Clicca per vedere i dettagli',
      fields: [
        { label: 'Data e ora', value: getAppointmentDateTime(appointment) },
        { label: 'Veterinario', value: appointment.doctorName },
      ],
    };
  }

  if (variant === 'doctor') {
    return {
      title: appointment.animalName,
      hint: 'Clicca per vedere i dettagli',
      fields: [
        { label: 'Data e ora', value: getAppointmentDateTime(appointment) },
        { label: 'Tipo visita', value: appointment.visitType },
      ],
    };
  }

  return {
    title: clientFullName,
    hint: 'Clicca per vedere i dettagli',
    fields: [
      { label: 'Data', value: appointment.appointmentDate },
      { label: 'Orario', value: appointment.appointmentHour },
      { label: 'Medico', value: appointment.doctorName },
    ],
  };
}

/**
 * @param {{
 *   appointment: Appointment,
 *   onEdit?: (appointment: Appointment) => void,
 *   onDelete?: (appointment: Appointment) => void,
 *   onDelayNotification?: (appointment: Appointment) => void,
 *   actions?: Array<{ label: string, className?: string, onClick?: (appointment: Appointment) => void }>,
 *   variant?: 'receptionist' | 'client' | 'doctor'
 * }} props
 */
export function AppointmentCard({
  appointment,
  onEdit,
  onDelete,
  onDelayNotification,
  actions = [],
  variant = 'receptionist'
}) {
  const [isExpanded, setIsExpanded] = useState(false);
  const clientFullName = getClientFullName(appointment);
  const summary = getSummaryConfig(appointment, variant);
  const cardActions = [
    onEdit && { label: 'Modifica', className: 'btn btn-outline btn-sm', onClick: onEdit },
    onDelete && { label: 'Cancella', className: 'btn btn-danger btn-sm', onClick: onDelete },
    onDelayNotification && { label: 'Not. Ritardo', className: 'btn btn-secondary btn-sm', onClick: onDelayNotification },
    ...actions,
  ].filter(Boolean);

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
        style={{ '--appointment-summary-field-count': summary.fields.length }}
        aria-expanded={isExpanded}
        onClick={toggleDetails}
      >
        <div className="appointment-card__client">
          <h3 className="appointment-card__title">{summary.title}</h3>
          <span className="appointment-card__hint">{summary.hint}</span>
        </div>

        {summary.fields.map((field) => (
          <div key={field.label} className="appointment-card__field">
            <span>{field.label}</span>
            <strong>{field.value}</strong>
          </div>
        ))}

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
            <span>Data</span>
            <strong>{appointment.appointmentDate}</strong>
          </div>

          <div className="appointment-card__field">
            <span>Orario</span>
            <strong>{appointment.appointmentHour}</strong>
          </div>

          <div className="appointment-card__field">
            <span>Medico</span>
            <strong>{appointment.doctorName}</strong>
          </div>

          <div className="appointment-card__field">
            <span>Stato</span>
            <strong>{appointment.status}</strong>
          </div>
        </div>
      )}

      {cardActions.length > 0 && (
        <div className="appointment-card__actions" aria-label="Azioni appuntamento">
          {cardActions.map((action) => (
            <button
              key={action.label}
              type="button"
              className={action.className ?? 'btn btn-outline btn-sm'}
              onClick={(event) => handleActionClick(event, action.onClick)}
            >
              {action.label}
            </button>
          ))}
        </div>
      )}
    </article>
  );
}

/**
 * @param {{
 *   appointments: Appointment[],
 *   onEdit?: (appointment: Appointment) => void,
 *   onDelete?: (appointment: Appointment) => void,
 *   onDelayNotification?: (appointment: Appointment) => void,
 *   actions?: Array<{ label: string, className?: string, onClick?: (appointment: Appointment) => void }>,
 *   variant?: 'receptionist' | 'client' | 'doctor'
 * }} props
 */
export function AppointmentCardList({
  appointments,
  onEdit,
  onDelete,
  onDelayNotification,
  actions = [],
  variant = 'receptionist'
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
          actions={actions}
          variant={variant}
        />
      ))}
    </div>
  );
}
