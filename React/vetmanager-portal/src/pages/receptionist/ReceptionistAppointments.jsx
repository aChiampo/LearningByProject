import { useContext } from 'react';
import { AppContext } from '../../context/AppContext';
import { ROLE_CONFIG } from '../../data/roleConfig';
import PageTitle from '../../components/common/PageTitle';
import EmptyMessage from '../../components/common/EmptyMessage';
import { AppointmentCardList } from '../../components/appointments/AppointmentCard';

export default function ReceptionistAppointments() {
  const { currentRole } = useContext(AppContext);
  const config = ROLE_CONFIG[currentRole];
  const appointments = config?.allAppointments ?? [];

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

        {appointments.length > 0 ? (
          <AppointmentCardList
            appointments={appointments}
            onEdit={handleEditAppointment}
            onDelete={handleDeleteAppointment}
            onDelayNotification={handleDelayNotification}
          />
        ) : (
          <EmptyMessage>Nessun appuntamento registrato a sistema.</EmptyMessage>
        )}
      </div>
    </div>
  );
}
