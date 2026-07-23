import { useContext, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppContext } from '../../context/AppContext';
import PageTitle from '../../components/common/PageTitle';
import EmptyMessage from '../../components/common/EmptyMessage';
import { fetchAnimalsByOwner } from '../../services/animalApi';
import { fetchAppointments } from '../../services/appointmentApi';

export default function ClientDashboard() {
  const navigate = useNavigate();
  const { currentUser } = useContext(AppContext);
  const ownerId = currentUser?.id;
  const [animals, setAnimals] = useState([]);
  const [appointments, setAppointments] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [loadError, setLoadError] = useState('');

  useEffect(() => {
    if (!ownerId) return;

    let isMounted = true;

    async function loadAnimals() {
      setIsLoading(true);
      setLoadError('');

      try {
        const [animalList, appointmentList] = await Promise.all([
          fetchAnimalsByOwner(ownerId),
          fetchAppointments({ clientID: ownerId }),
        ]);

        if (isMounted) {
          setAnimals(animalList);
          setAppointments(appointmentList);
        }
      } catch {
        if (isMounted) {
          setLoadError(error.message);
          setAnimals([]);
          setAppointments([]);
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    loadAnimals();

    return () => {
      isMounted = false;
    };
  }, [ownerId]);

  return (
    <div>
      <PageTitle eyebrow="Area Riservata" title="Dashboard Cliente" />

      <section className="section-block">
        <div className="section-head">
          <h2>I tuoi animali</h2>
          <button className="btn btn-primary btn-sm" type="button" onClick={() => navigate('/client/add-animal')}>
            Aggiungi Animale
          </button>
        </div>

        {isLoading && <p className="muted-text">Caricamento animali...</p>}

        {loadError && (
          <p className="form-status form-status--error">
            {loadError}
          </p>
        )}

        {!isLoading && animals.length > 0 ? (
          <div className="grid-cards">
            {animals.map((animale, index) => (
              <article key={index} className="panel card">
                <div className="card-header">
                  <h3>{animale.nome}</h3>
                  <span className="badge">{animale.specie}</span>
                </div>
                <p className="muted-text">Razza: {animale.razza}</p>
                <p className="muted-text">Eta: {new Date().getFullYear() - new Date(animale.dataNascita).getFullYear()}</p>
                <div className="actions-row">
                  <button className="btn btn-primary btn-sm" onClick={() => navigate('/client/booking')}>
                    Prenota Visita
                  </button>
                  <button className="btn btn-outline btn-sm">Apri Cartella</button>
                </div>
              </article>
            ))}
          </div>
        ) : !isLoading && (
          <>
            <EmptyMessage>Nessun animale registrato nel tuo profilo.</EmptyMessage>
            <div className="page-actions">
              <button className="btn btn-primary" type="button" onClick={() => navigate('/client/add-animal')}>
                Aggiungi Animale
              </button>
            </div>
          </>
        )}
      </section>

      <section className="section-block section-spaced">
        <h2>Prossimi Appuntamenti</h2>

        {appointments.length > 0 ? (
          <div className="panel table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Animale</th>
                  <th>Data e Ora</th>
                  <th>Prestazione</th>
                  <th>Veterinario</th>
                  <th>Stato</th>
                </tr>
              </thead>
              <tbody>
                {appointments.map((appointment, index) => (
                  <tr key={index}>
                    <td><strong>{appointment.animalName}</strong></td>
                    <td>{appointment.appointmentDate || appointment.appointmentHour}</td>
                    <td>{appointment.visitType}</td>
                    <td>{appointment.doctorName}</td>
                    <td><span className="badge badge-success">{appointment.status}</span></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <EmptyMessage>Non ci sono appuntamenti in programma.</EmptyMessage>
        )}
      </section>
    </div>
  );
}
