import { useContext, useEffect, useState } from 'react';
import { AppContext } from '../../context/AppContext';
import PageTitle from '../../components/common/PageTitle';
import EmptyMessage from '../../components/common/EmptyMessage';
import { fetchAppointments } from '../../services/appointmentApi';

function getLocalStartOfToday() {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, '0');
  const day = String(today.getDate()).padStart(2, '0');

  return `${year}-${month}-${day}T00:00:00`;
}

export default function DoctorDashboard() {
  const { currentUser } = useContext(AppContext);
  const doctorId = currentUser?.id;
  const [todayAppointments, setTodayAppointments] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [loadError, setLoadError] = useState('');

  useEffect(() => {
    if (!doctorId) return;

    let isMounted = true;

    async function loadTodayAppointments() {
      setIsLoading(true);
      setLoadError('');

      try {
        const appointmentList = await fetchAppointments({
          doctorID: doctorId,
          date: getLocalStartOfToday(),
        });

        if (isMounted) {
          setTodayAppointments(appointmentList);
        }
      } catch (error) {
        if (isMounted) {
          setLoadError(error.message);
          setTodayAppointments([]);
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    loadTodayAppointments();

    return () => {
      isMounted = false;
    };
  }, [doctorId]);

  return (
    <div>
      <PageTitle eyebrow="Area Professionale" title="Dashboard Clinica" />

      <div className="panel section-spaced-sm">
        <h2>Agenda Visite di Oggi</h2>
        <p className="muted-text">Controllo appuntamenti attivi</p>

        {isLoading && <p className="muted-text">Caricamento agenda...</p>}

        {loadError && (
          <p className="form-status form-status--error">
            {loadError}
          </p>
        )}

        {!isLoading && todayAppointments.length > 0 ? (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Orario</th>
                  <th>Paziente</th>
                  <th>Proprietario</th>
                  <th>Motivo</th>
                  <th>Azioni</th>
                </tr>
              </thead>
              <tbody>
                {todayAppointments.map((visit, index) => (
                  <tr key={index}>
                    <td><strong>{visit.appointmentHour}</strong></td>
                    <td>{visit.animalName} {visit.animalBreed ? `(${visit.animalBreed})` : ''}</td>
                    <td>{visit.ownerName}</td>
                    <td>{visit.visitType}</td>
                    <td>
                      <button className="btn btn-primary btn-sm" onClick={() => window.alert(`Apertura cartella clinica di ${visit.animalName}`)}>
                        Visita
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : !isLoading && (
          <EmptyMessage>Nessun appuntamento programmato per la giornata di oggi.</EmptyMessage>
        )}
      </div>
    </div>
  );
}
