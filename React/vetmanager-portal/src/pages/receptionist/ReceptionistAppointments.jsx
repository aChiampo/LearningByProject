import { useEffect, useState } from 'react';
import PageTitle from '../../components/common/PageTitle';
import EmptyMessage from '../../components/common/EmptyMessage';
import { AppointmentCardList } from '../../components/appointments/AppointmentCard';
import { fetchAppointments } from '../../services/appointmentApi';

export default function ReceptionistAppointments() {
  const [appointments, setAppointments] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [loadError, setLoadError] = useState('');

  useEffect(() => {
    let isMounted = true;

    async function loadAppointments() {
      setIsLoading(true);
      setLoadError('');

      try {
        const appointmentList = await fetchAppointments();

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

  function handleEditAppointment() {
    window.alert('Modifica appuntamento');
  }

  function handleDeleteAppointment() {
    window.alert('Appuntamento eliminato');
  }

  function handleDelayNotification() {
    window.alert('Invio notifica di ritardo');
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
    </div>
  );
}
