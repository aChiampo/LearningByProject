import { useContext, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppContext } from '../../context/AppContext';
import PageTitle from '../../components/common/PageTitle';
import EmptyMessage from '../../components/common/EmptyMessage';
import { AppointmentCardList } from '../../components/appointments/AppointmentCard';
import AppointmentRescheduleDialog from '../../components/appointments/AppointmentRescheduleDialog';
import { fetchAnimalsByOwner } from '../../services/animalApi';
import { deleteAppointment, fetchAppointments, getLocalStartOfToday } from '../../services/appointmentApi';
import { fetchVisitTypes } from '../../services/visitTypeApi';

function createAnimalLookup(animals) {
  return new Map(
    animals
      .map((animal) => [animal.id, animal])
      .filter(([id]) => id !== null && id !== undefined)
  );
}

function createVisitTypeLookup(visitTypes) {
  return new Map(
    visitTypes
      .map((visitType) => [visitType.id, visitType])
      .filter(([id]) => id !== null && id !== undefined)
  );
}

export default function ClientDashboard() {
  const navigate = useNavigate();
  const { currentUser } = useContext(AppContext);
  const ownerId = currentUser?.id;
  const [animals, setAnimals] = useState([]);
  const [appointments, setAppointments] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [loadError, setLoadError] = useState('');
  const [appointmentToEdit, setAppointmentToEdit] = useState(null);
  const [appointmentStatus, setAppointmentStatus] = useState('');

  useEffect(() => {
    if (!ownerId) return;

    let isMounted = true;

    async function loadAnimals() {
      setIsLoading(true);
      setLoadError('');

      try {
        const [animalList, visitTypeList] = await Promise.all([
          fetchAnimalsByOwner(ownerId),
          fetchVisitTypes(),
        ]);
        const appointmentList = await fetchAppointments({
            clientID: ownerId,
            date: getLocalStartOfToday(),
          }, {
            loadLookups: false,
            lookups: {
              animalsById: createAnimalLookup(animalList),
              visitTypesById: createVisitTypeLookup(visitTypeList),
            },
          });

        if (isMounted) {
          setAnimals(animalList);
          setAppointments(appointmentList);
        }
      } catch (error) {
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

  async function reloadAppointments() {
    if (!ownerId) {
      return;
    }

    const visitTypeList = await fetchVisitTypes();
    const appointmentList = await fetchAppointments({
      clientID: ownerId,
      date: getLocalStartOfToday(),
    }, {
      loadLookups: false,
      lookups: {
        animalsById: createAnimalLookup(animals),
        visitTypesById: createVisitTypeLookup(visitTypeList),
      },
    });

    setAppointments(appointmentList);
  }

  function handleEditAppointment(appointment) {
    setAppointmentToEdit(appointment);
    setAppointmentStatus('');
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
      setAppointmentStatus('Appuntamento cancellato. Il cliente ricevera una notifica email.');
      setLoadError('');
    } catch (error) {
      setLoadError(error.message);
    }
  }

  async function handleAppointmentSaved() {
    await reloadAppointments();
    setAppointmentToEdit(null);
    setAppointmentStatus('Appuntamento modificato. Il cliente ricevera una notifica email.');
  }

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

        {appointmentStatus && (
          <p className="form-status form-status--success">
            {appointmentStatus}
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
                  <button
                    className="btn btn-outline btn-sm"
                    type="button"
                    onClick={() => navigate(`/client/animals/${animale.id}/cartella`)}
                  >
                    Apri Cartella
                  </button>
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
          <AppointmentCardList
            appointments={appointments}
            variant="client"
            onEdit={handleEditAppointment}
            onDelete={handleDeleteAppointment}
          />
        ) : (
          <EmptyMessage>Non ci sono appuntamenti in programma.</EmptyMessage>
        )}
      </section>

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
