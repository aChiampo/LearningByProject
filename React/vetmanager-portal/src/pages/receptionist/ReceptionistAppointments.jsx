import { useEffect, useState } from 'react';
import PageTitle from '../../components/common/PageTitle';
import EmptyMessage from '../../components/common/EmptyMessage';
import { AppointmentCardList } from '../../components/appointments/AppointmentCard';
import AppointmentRescheduleDialog from '../../components/appointments/AppointmentRescheduleDialog';
import { deleteAppointment, fetchAppointments, getLocalStartOfToday, sendDelayNotification } from '../../services/appointmentApi';

export default function ReceptionistAppointments() {
  const [appointments, setAppointments] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [loadError, setLoadError] = useState('');
  const [delayDialogVisit, setDelayDialogVisit] = useState(null);
  const [delayMinutes, setDelayMinutes] = useState('15');
  const [delayError, setDelayError] = useState('');
  const [delayStatus, setDelayStatus] = useState('');
  const [isSendingDelay, setIsSendingDelay] = useState(false);
  const [appointmentToEdit, setAppointmentToEdit] = useState(null);

  useEffect(() => {
    let isMounted = true;

    async function loadAppointments() {
      setIsLoading(true);
      setLoadError('');

      try {
        const appointmentList = await fetchAppointments({
          date: getLocalStartOfToday(),
        });

        if (isMounted) {
          setAppointments(appointmentList);
        }
      } catch (error) {
        if (isMounted) {
          setLoadError(error.message);
          setAppointments([]);
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    loadAppointments();

    return () => {
      isMounted = false;
    };
  }, []);

  async function reloadAppointments() {
    const appointmentList = await fetchAppointments({
      date: getLocalStartOfToday(),
    });

    setAppointments(appointmentList);
  }

  function handleEditAppointment(appointment) {
    setAppointmentToEdit(appointment);
    setDelayStatus('');
  }

  async function handleDeleteAppointment(appointment) {
    const confirmed = window.confirm(`Cancellare l'appuntamento di ${appointment.animalName}?`);

    if (!confirmed) {
      return;
    }

    try {
      await deleteAppointment(appointment.id);
      setAppointments((currentAppointments) => (
        currentAppointments.filter((currentAppointment) => currentAppointment.id !== appointment.id)
      ));
      setDelayStatus('Appuntamento cancellato. Il cliente ricevera una notifica email.');
      setLoadError('');
    } catch (error) {
      setLoadError(error.message);
    }
  }

  async function handleAppointmentSaved() {
    await reloadAppointments();
    setAppointmentToEdit(null);
    setDelayStatus('Appuntamento modificato. Il cliente ricevera una notifica email.');
  }

  function handleDelayNotification(appointment) {
    setDelayDialogVisit(appointment);
    setDelayMinutes('15');
    setDelayError('');
    setDelayStatus('');
  }

  function closeDelayDialog() {
    if (isSendingDelay) {
      return;
    }

    setDelayDialogVisit(null);
    setDelayError('');
  }

  async function handleDelaySubmit(event) {
    event.preventDefault();
    const parsedDelay = Number(delayMinutes);

    if (!Number.isInteger(parsedDelay) || parsedDelay <= 0) {
      setDelayError('Inserisci un ritardo valido in minuti.');
      return;
    }

    setIsSendingDelay(true);
    setDelayError('');
    setDelayStatus('');

    try {
      await sendDelayNotification(delayDialogVisit.id, parsedDelay);
      setDelayStatus(`Notifica di ritardo inviata per ${delayDialogVisit.animalName || 'la visita'}.`);
      setDelayDialogVisit(null);
    } catch (error) {
      setDelayError(error.message);
    } finally {
      setIsSendingDelay(false);
    }
  }

  return (
    <div>
      <PageTitle eyebrow="Planning" title="Gestione Appuntamenti" />

      <div className="panel section-spaced-sm">
        <div className="toolbar-row">
          <h2>Calendario Generale</h2>
          <button className="btn btn-primary btn-sm" onClick={() => window.alert('Apertura popup per inserire un appuntamento sul posto')}>
            + Nuovo Appuntamento
          </button>
        </div>

        {isLoading && <p className="muted-text">Caricamento appuntamenti...</p>}

        {loadError && (
          <p className="form-status form-status--error">
            {loadError}
          </p>
        )}

        {delayStatus && (
          <p className="form-status form-status--success">
            {delayStatus}
          </p>
        )}

        {!isLoading && appointments.length > 0 ? (
          <AppointmentCardList
            appointments={appointments}
            onEdit={handleEditAppointment}
            onDelete={handleDeleteAppointment}
            onDelayNotification={handleDelayNotification}
          />
        ) : !isLoading && (
          <EmptyMessage>Nessun appuntamento registrato a sistema.</EmptyMessage>
        )}
      </div>

      {delayDialogVisit && (
        <div className="profile-dialog" role="presentation">
          <div className="profile-dialog__backdrop" onClick={closeDelayDialog} aria-hidden="true"></div>
          <section className="profile-dialog__window" role="dialog" aria-modal="true" aria-labelledby="delay-dialog-title">
            <div className="card-header">
              <h2 id="delay-dialog-title">Notifica ritardo</h2>
              <button type="button" className="btn btn-outline btn-sm" onClick={closeDelayDialog} disabled={isSendingDelay}>
                Chiudi
              </button>
            </div>

            <p className="muted-text">
              {delayDialogVisit.animalName || 'Visita'} - {delayDialogVisit.appointmentDate} {delayDialogVisit.appointmentHour}
            </p>

            <form className="stack-form" onSubmit={handleDelaySubmit}>
              <label htmlFor="delay-minutes">
                Ritardo stimato in minuti
                <input
                  id="delay-minutes"
                  name="delayMinutes"
                  type="number"
                  min="1"
                  step="1"
                  value={delayMinutes}
                  onChange={(event) => setDelayMinutes(event.target.value)}
                  required
                />
              </label>

              {delayError && (
                <p className="form-status form-status--error">{delayError}</p>
              )}

              <div className="actions-row">
                <button type="submit" className="btn btn-primary" disabled={isSendingDelay}>
                  {isSendingDelay ? 'Invio...' : 'Invia notifica'}
                </button>
                <button type="button" className="btn btn-outline" onClick={closeDelayDialog} disabled={isSendingDelay}>
                  Annulla
                </button>
              </div>
            </form>
          </section>
        </div>
      )}

      {appointmentToEdit && (
        <AppointmentRescheduleDialog
          appointment={appointmentToEdit}
          onClose={() => setAppointmentToEdit(null)}
          onSaved={handleAppointmentSaved}
        />
      )}
    </div>
  );
}
