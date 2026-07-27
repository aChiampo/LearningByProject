import { useContext, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppContext } from '../../context/AppContext';
import PageTitle from '../../components/common/PageTitle';
import EmptyMessage from '../../components/common/EmptyMessage';
import AddAnimalForm from '../../components/animals/AddAnimalForm';
import { AppointmentCardList } from '../../components/appointments/AppointmentCard';
import { fetchAppointments, getLocalStartOfToday } from '../../services/appointmentApi';
import {
  closeAnimalEvaluationRequest,
  fetchOpenAnimalEvaluationRequests,
} from '../../services/animalApi';

export default function DoctorDashboard() {
  const navigate = useNavigate();
  const { currentUser } = useContext(AppContext);
  const doctorId = currentUser?.id;

  const [todayAppointments, setTodayAppointments] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [loadError, setLoadError] = useState('');

  const [animalRequests, setAnimalRequests] = useState([]);
  const [selectedRequestId, setSelectedRequestId] = useState(null);
  const [prefillRequestId, setPrefillRequestId] = useState(null);
  const [isLoadingRequests, setIsLoadingRequests] = useState(false);
  const [requestError, setRequestError] = useState('');

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

  useEffect(() => {
    let isMounted = true;

    async function loadRequests() {
      setIsLoadingRequests(true);
      setRequestError('');

      try {
        const requests = await fetchOpenAnimalEvaluationRequests();

        if (isMounted) {
          setAnimalRequests(requests);
        }
      } catch (error) {
        if (isMounted) {
          setRequestError(error.message);
        }
      } finally {
        if (isMounted) {
          setIsLoadingRequests(false);
        }
      }
    }

    loadRequests();

    return () => {
      isMounted = false;
    };
  }, []);

  const selectedRequest = animalRequests.find((request) => request.id === selectedRequestId);
  const prefillRequest = animalRequests.find((request) => request.id === prefillRequestId);

  async function handleCloseRequest(requestId) {
    try {
      await closeAnimalEvaluationRequest(requestId);
      setAnimalRequests((currentRequests) => currentRequests.filter((request) => request.id !== requestId));
      setSelectedRequestId(null);
      setPrefillRequestId(null);
      setRequestError('');
    } catch (error) {
      setRequestError(error.message);
    }
  }

  return (
    <div>
      <PageTitle eyebrow="Area Professionale" title="Dashboard Clinica" />

      <section className="panel section-spaced-sm">
        <div className="toolbar-row">
          <div>
            <h2>Richieste nuovi animali</h2>
            <p className="muted-text">Valutazioni inviate dai clienti</p>
          </div>
        </div>

        {isLoadingRequests && <p className="muted-text">Caricamento richieste...</p>}

        {requestError && (
          <p className="form-status form-status--error">{requestError}</p>
        )}

        {!isLoadingRequests && animalRequests.length > 0 ? (
          <div className="grid-cards">
            {animalRequests.map((request) => (
              <button
                key={request.id}
                className="panel data-card animal-request-card"
                type="button"
                onClick={() => {
                  setSelectedRequestId((currentId) => (currentId === request.id ? null : request.id));
                  setPrefillRequestId(null);
                }}
              >
                <span className="badge">Richiesta nuovo animale</span>
                <h3>{request.nome}</h3>
                <p>{request.nomeCliente}</p>
                <small>{request.specie} / {request.razza}</small>
              </button>
            ))}
          </div>
        ) : !isLoadingRequests && (
          <EmptyMessage>Nessuna richiesta nuovo animale in attesa.</EmptyMessage>
        )}

        {selectedRequest && (
          <div className="panel section-spaced-sm">
            <div className="card-header">
              <h3>Richiesta nuovo animale</h3>
              <span className="badge">{selectedRequest.nomeCliente}</span>
            </div>
            <dl className="details section-spaced-sm">
              <div>
                <dt>Animale</dt>
                <dd>{selectedRequest.nome}</dd>
              </div>
              <div>
                <dt>Specie</dt>
                <dd>{selectedRequest.specie}</dd>
              </div>
              <div>
                <dt>Razza</dt>
                <dd>{selectedRequest.razza}</dd>
              </div>
              <div>
                <dt>Email cliente</dt>
                <dd>{selectedRequest.emailCliente}</dd>
              </div>
            </dl>
            <p className="muted-text section-spaced-sm">{selectedRequest.descrizione}</p>
            {selectedRequest.note && (
              <p className="muted-text">Note: {selectedRequest.note}</p>
            )}
            <div className="actions-row">
              <button className="btn btn-secondary" type="button" onClick={() => navigate('/doctor/management')}>
                Aggiungi Razza o Specie
              </button>
              <button className="btn btn-primary" type="button" onClick={() => setPrefillRequestId(selectedRequest.id)}>
                Aggiungi Animale
              </button>
              <button className="btn btn-outline" type="button" onClick={() => handleCloseRequest(selectedRequest.id)}>
                Chiudi richiesta
              </button>
            </div>
          </div>
        )}

        {prefillRequest && (
          <div className="section-spaced-sm">
            <AddAnimalForm
              initialValues={prefillRequest}
              onCreated={() => handleCloseRequest(prefillRequest.id)}
            />
          </div>
        )}
      </section>

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
          <AppointmentCardList
            appointments={todayAppointments}
            actions={[
              {
                label: 'Visita',
                className: 'btn btn-primary btn-sm',
                onClick: (visit) => window.alert(`Apertura cartella clinica di ${visit.animalName}`),
              },
            ]}
          />
        ) : !isLoading && (
          <EmptyMessage>Nessun appuntamento programmato per la giornata di oggi.</EmptyMessage>
        )}
      </div>
    </div>
  );
}
