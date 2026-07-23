import { useEffect, useState } from 'react';
import PageTitle from '../../components/common/PageTitle';
import { fetchAppointments } from '../../services/appointmentApi';
import { fetchUnpaidVisits } from '../../services/paymentApi';
import { fetchClients } from '../../services/userApi';

function isToday(value) {
  if (!value) {
    return false;
  }

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return false;
  }

  const today = new Date();
  return date.toDateString() === today.toDateString();
}

function getAppointmentDate(appointment) {
  return appointment.raw?.dataVisita ?? appointment.raw?.date ?? appointment.raw?.data;
}

export default function ReceptionistDashboard() {
  const [stats, setStats] = useState({
    todayAppointmentsCount: 0,
    newClientsCount: 0,
    pendingAmount: 0,
  });
  const [isLoading, setIsLoading] = useState(false);
  const [loadError, setLoadError] = useState('');

  useEffect(() => {
    let isMounted = true;

    async function loadDashboardStats() {
      setIsLoading(true);
      setLoadError('');

      try {
        const [appointmentList, clientList, unpaidVisitList] = await Promise.all([
          fetchAppointments(),
          fetchClients(),
          fetchUnpaidVisits(),
        ]);

        if (isMounted) {
          setStats({
            todayAppointmentsCount: appointmentList.filter((appointment) => isToday(getAppointmentDate(appointment))).length,
            newClientsCount: clientList.length,
            pendingAmount: unpaidVisitList.reduce((total, visit) => total + (visit.totalAmount ?? 0), 0),
          });
        }
      } catch (error) {
        if (isMounted) {
          setLoadError(error.message);
          setStats({
            todayAppointmentsCount: 0,
            newClientsCount: 0,
            pendingAmount: 0,
          });
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    loadDashboardStats();

    return () => {
      isMounted = false;
    };
  }, []);

  return (
    <div>
      <PageTitle eyebrow="Gestione Studio" title="Dashboard Reception" />

      {isLoading && <p className="muted-text">Caricamento riepilogo...</p>}

      {loadError && (
        <p className="form-status form-status--error">
          {loadError}
        </p>
      )}

      <div className="stats-grid">
        <div className="panel stat-panel">
          <strong className="stat-value stat-value--primary">{stats.todayAppointmentsCount}</strong>
          <p>Appuntamenti Oggi</p>
        </div>
        <div className="panel stat-panel">
          <strong className="stat-value stat-value--secondary">{stats.newClientsCount}</strong>
          <p>Nuovi Clienti</p>
        </div>
        <div className="panel stat-panel">
          <strong className="stat-value stat-value--danger">Euro {stats.pendingAmount}</strong>
          <p>In Sospeso</p>
        </div>
      </div>
    </div>
  );
}
