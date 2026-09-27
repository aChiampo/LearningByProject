import { useEffect, useMemo, useState } from 'react';
import { fetchAvailableSlots, rescheduleAppointment } from '../../services/appointmentApi';

function getTomorrowDate() {
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  return tomorrow.toISOString().slice(0, 10);
}

function getAppointmentDateValue(appointment) {
  const dateValue = appointment?.dataVisita ?? appointment?.raw?.dataVisita;
  if (!dateValue) {
    return getTomorrowDate();
  }

  const date = new Date(dateValue);
  if (Number.isNaN(date.getTime())) {
    return getTomorrowDate();
  }

  const formattedDate = date.toISOString().slice(0, 10);
  return formattedDate > getTomorrowDate() ? formattedDate : getTomorrowDate();
}

export default function AppointmentRescheduleDialog({
  appointment,
  onClose,
  onSaved,
}) {
  const minDate = useMemo(() => getTomorrowDate(), []);
  const [selectedDate, setSelectedDate] = useState(() => getAppointmentDateValue(appointment));
  const [availableSlots, setAvailableSlots] = useState([]);
  const [selectedSlot, setSelectedSlot] = useState('');
  const [slotMessage, setSlotMessage] = useState('');
  const [isLoadingSlots, setIsLoadingSlots] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    let isMounted = true;

    async function loadSlots() {
      setSelectedSlot('');
      setAvailableSlots([]);
      setSlotMessage('');
      setError('');

      if (!appointment?.animalId || !appointment?.visitTypeId || !appointment?.doctorId || !selectedDate) {
        setSlotMessage('Dati appuntamento incompleti.');
        return;
      }

      if (selectedDate < minDate) {
        setSlotMessage('Seleziona una data futura.');
        return;
      }

      setIsLoadingSlots(true);

      try {
        const slots = await fetchAvailableSlots({
          animaleId: appointment.animalId,
          tipoVisitaId: appointment.visitTypeId,
          veterinarioId: appointment.doctorId,
          data: selectedDate,
        });

        if (isMounted) {
          setAvailableSlots(slots);
          setSlotMessage(slots.length ? '' : "Nessuno slot disponibile. Seleziona un'altra data.");
        }
      } catch (slotError) {
        if (isMounted) {
          setSlotMessage(slotError.message || "Nessuno slot disponibile. Seleziona un'altra data.");
        }
      } finally {
        if (isMounted) {
          setIsLoadingSlots(false);
        }
      }
    }

    loadSlots();

    return () => {
      isMounted = false;
    };
  }, [appointment, selectedDate, minDate]);

  async function handleSubmit(event) {
    event.preventDefault();

    if (!selectedSlot) {
      setError('Seleziona uno slot disponibile.');
      return;
    }

    setIsSaving(true);
    setError('');

    try {
      await rescheduleAppointment(appointment.id, selectedSlot);
      onSaved?.();
    } catch (saveError) {
      setError(saveError.message || 'Impossibile modificare l\'appuntamento.');
    } finally {
      setIsSaving(false);
    }
  }

  return (
    <div className="profile-dialog" role="presentation">
      <div className="profile-dialog__backdrop" onClick={isSaving ? undefined : onClose} aria-hidden="true"></div>
      <section className="profile-dialog__window" role="dialog" aria-modal="true" aria-labelledby="reschedule-dialog-title">
        <div className="card-header">
          <h2 id="reschedule-dialog-title">Modifica appuntamento</h2>
          <button type="button" className="btn btn-outline btn-sm" onClick={onClose} disabled={isSaving}>
            Chiudi
          </button>
        </div>

        <p className="muted-text">
          {appointment.animalName} - {appointment.appointmentDate} {appointment.appointmentHour}
        </p>

        <form className="stack-form" onSubmit={handleSubmit}>
          <label htmlFor="reschedule-date">
            Nuova data
            <input
              id="reschedule-date"
              type="date"
              value={selectedDate}
              min={minDate}
              onChange={(event) => setSelectedDate(event.target.value)}
              required
            />
          </label>

          <div className="appointment-slots" aria-live="polite">
            <div className="appointment-slots__header">
              <strong>Slot disponibili</strong>
              {isLoadingSlots && <span>Ricerca in corso...</span>}
            </div>

            {!isLoadingSlots && slotMessage && (
              <p className="appointment-slots__message">{slotMessage}</p>
            )}

            {!isLoadingSlots && availableSlots.length > 0 && (
              <div className="appointment-slots__grid">
                {availableSlots.map((slot) => (
                  <button
                    key={slot.inizio}
                    type="button"
                    className={`appointment-slot${selectedSlot === slot.inizio ? ' appointment-slot--selected' : ''}`}
                    onClick={() => setSelectedSlot(slot.inizio)}
                  >
                    {slot.label}
                  </button>
                ))}
              </div>
            )}
          </div>

          {error && <p className="form-status form-status--error">{error}</p>}

          <div className="actions-row">
            <button type="submit" className="btn btn-primary" disabled={isSaving || isLoadingSlots}>
              {isSaving ? 'Salvataggio...' : 'Salva modifiche'}
            </button>
            <button type="button" className="btn btn-outline" onClick={onClose} disabled={isSaving}>
              Annulla
            </button>
          </div>
        </form>
      </section>
    </div>
  );
}
