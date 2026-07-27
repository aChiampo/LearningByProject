import { useEffect, useMemo, useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppContext } from '../../context/AppContext';
import PageTitle from '../../components/common/PageTitle';
import { fetchAllAnimals, fetchAnimalsByOwner } from '../../services/animalApi';
import { fetchDoctors } from '../../services/userApi';
import { fetchVisitTypes } from '../../services/visitTypeApi';
import { bookAppointment, fetchAvailableSlots } from '../../services/appointmentApi';
import './ClientBooking.css';

function getTomorrowDate() {
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  return tomorrow.toISOString().slice(0, 10);
}

function formatSlotDateTime(value) {
  if (!value) {
    return '';
  }

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return new Intl.DateTimeFormat('it-IT', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(date);
}

export default function ClientBooking() {
  const navigate = useNavigate();
  const { currentUser, currentRole } = useContext(AppContext);
  const ownerId = currentUser?.id;
  const isReceptionist = currentRole === 'receptionist' || currentUser?.backendRole === 'RECEPTIONIST';
  const backPath = isReceptionist ? '/receptionist/appointments' : '/client/dashboard';
  const minDate = useMemo(getTomorrowDate, []);

  const [formData, setFormData] = useState({
    animaleId: '',
    tipoVisitaId: '',
    veterinarioId: '',
    data: '',
    dataVisita: '',
    note: '',
  });

  const [animals, setAnimals] = useState([]);
  const [visitTypes, setVisitTypes] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [availableSlots, setAvailableSlots] = useState([]);
  const [isOptionsLoading, setIsOptionsLoading] = useState(false);
  const [isSlotsLoading, setIsSlotsLoading] = useState(false);
  const [isBookingLoading, setIsBookingLoading] = useState(false);
  const [error, setError] = useState(null);
  const [slotMessage, setSlotMessage] = useState('');
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    if (!ownerId && !isReceptionist) return;

    let isMounted = true;

    async function loadOptions() {
      setIsOptionsLoading(true);
      setError(null);

      try {
        const [animalList, visitTypeList, doctorList] = await Promise.all([
          isReceptionist ? fetchAllAnimals() : fetchAnimalsByOwner(ownerId),
          fetchVisitTypes(),
          fetchDoctors(),
        ]);

        if (isMounted) {
          setAnimals(animalList);
          setVisitTypes(visitTypeList);
          setDoctors(doctorList);
        }
      } catch (loadError) {
        if (isMounted) {
          setError(loadError.message || 'Non e stato possibile caricare i dati della prenotazione.');
          setAnimals([]);
          setVisitTypes([]);
          setDoctors([]);
        }
      } finally {
        if (isMounted) {
          setIsOptionsLoading(false);
        }
      }
    }

    loadOptions();

    return () => {
      isMounted = false;
    };
  }, [ownerId, isReceptionist]);

  useEffect(() => {
    const { animaleId, tipoVisitaId, veterinarioId, data } = formData;

    setAvailableSlots([]);
    setSlotMessage('');
    setFormData((currentData) => (
      currentData.dataVisita ? { ...currentData, dataVisita: '' } : currentData
    ));

    if (!animaleId || !tipoVisitaId || !veterinarioId || !data) {
      return undefined;
    }

    if (data < minDate) {
      setSlotMessage('Seleziona una data futura per cercare gli slot disponibili.');
      return undefined;
    }

    let isMounted = true;

    async function loadSlots() {
      setIsSlotsLoading(true);
      setError(null);

      try {
        const slots = await fetchAvailableSlots({
          animaleId: parseInt(animaleId, 10),
          tipoVisitaId: parseInt(tipoVisitaId, 10),
          veterinarioId: parseInt(veterinarioId, 10),
          data,
        });

        if (isMounted) {
          setAvailableSlots(slots);
          setSlotMessage(slots.length ? '' : "Nessuno slot disponibile. Seleziona un'altra data.");
        }
      } catch (slotError) {
        if (isMounted) {
          setAvailableSlots([]);
          setSlotMessage(slotError.message || "Nessuno slot disponibile. Seleziona un'altra data.");
        }
      } finally {
        if (isMounted) {
          setIsSlotsLoading(false);
        }
      }
    }

    loadSlots();

    return () => {
      isMounted = false;
    };
  }, [formData.animaleId, formData.tipoVisitaId, formData.veterinarioId, formData.data, minDate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setSuccess(false);
    setFormData((currentData) => ({
      ...currentData,
      [name]: value,
    }));
  };

  const handleSlotSelect = (slotStart) => {
    setSuccess(false);
    setFormData((currentData) => ({
      ...currentData,
      dataVisita: slotStart,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(false);

    const animaleId = parseInt(formData.animaleId, 10);
    const tipoVisitaId = parseInt(formData.tipoVisitaId, 10);
    const veterinarioId = parseInt(formData.veterinarioId, 10);

    if (Number.isNaN(animaleId)) {
      setError('Seleziona un animale valido prima di continuare.');
      return;
    }
    if (Number.isNaN(tipoVisitaId)) {
      setError('Seleziona un tipo di visita valido.');
      return;
    }
    if (Number.isNaN(veterinarioId)) {
      setError('Seleziona un veterinario valido.');
      return;
    }
    if (!formData.data || formData.data < minDate) {
      setError('Seleziona una data futura per la visita.');
      return;
    }
    if (!formData.dataVisita) {
      setError('Seleziona uno slot disponibile.');
      return;
    }

    setIsBookingLoading(true);

    try {
      await bookAppointment({
        animaleId,
        tipoVisitaId,
        veterinarioId,
        dataVisita: formData.dataVisita,
        note: formData.note,
      });

      setSuccess(true);
      setTimeout(() => {
        navigate(backPath);
      }, 1500);
    } catch (err) {
      setError(err.message || 'Si e verificato un errore durante la prenotazione.');
    } finally {
      setIsBookingLoading(false);
    }
  };

  return (
    <div>
      <PageTitle eyebrow="Nuova prenotazione" title="Prenota appuntamento" />
      <div className="panel panel-narrow booking-panel">
        <form className="stack-form booking-form" onSubmit={handleSubmit}>
          {isOptionsLoading && <p className="muted-text">Caricamento opzioni...</p>}
          {error && <p className="form-status form-status--error">{error}</p>}
          {success && <p className="form-status form-status--success">Appuntamento prenotato con successo.</p>}

          <label>
            Animale
            <select
              name="animaleId"
              value={formData.animaleId}
              onChange={handleChange}
              required
            >
              <option value="">Seleziona un animale</option>
              {animals.length > 0 ? (
                animals.map((animale) => (
                  <option key={animale.id} value={animale.id}>
                    {animale.nome} ({animale.specie || 'Specie non indicata'})
                  </option>
                ))
              ) : (
                <option value="" disabled>
                  Nessun animale disponibile
                </option>
              )}
            </select>
          </label>

          <label>
            Tipo di visita
            <select
              name="tipoVisitaId"
              value={formData.tipoVisitaId}
              onChange={handleChange}
              required
            >
              <option value="">Seleziona un tipo</option>
              {visitTypes.map((visitType) => (
                <option key={visitType.id} value={visitType.id}>
                  {visitType.nome}
                </option>
              ))}
            </select>
          </label>

          <label>
            Veterinario
            <select
              name="veterinarioId"
              value={formData.veterinarioId}
              onChange={handleChange}
              required
            >
              <option value="">Seleziona un veterinario</option>
              {doctors.map((doctor) => (
                <option key={doctor.id} value={doctor.id}>
                  {[doctor.nome, doctor.cognome].filter(Boolean).join(' ') || doctor.email}
                </option>
              ))}
            </select>
          </label>

          <label>
            Data
            <input
              type="date"
              name="data"
              value={formData.data}
              min={minDate}
              onChange={handleChange}
              required
            />
          </label>

          <div className="booking-slots" aria-live="polite">
            <div className="booking-slots__header">
              <strong>Slot disponibili</strong>
              {isSlotsLoading && <span>Ricerca in corso...</span>}
            </div>

            {!isSlotsLoading && slotMessage && (
              <p className="booking-slots__message">{slotMessage}</p>
            )}

            {!isSlotsLoading && availableSlots.length > 0 && (
              <div className="booking-slots__grid">
                {availableSlots.map((slot) => (
                  <button
                    key={slot.inizio}
                    type="button"
                    className={`booking-slot${formData.dataVisita === slot.inizio ? ' booking-slot--selected' : ''}`}
                    onClick={() => handleSlotSelect(slot.inizio)}
                  >
                    {slot.label || formatSlotDateTime(slot.inizio)}
                  </button>
                ))}
              </div>
            )}
          </div>

          <label>
            Note
            <textarea
              name="note"
              value={formData.note}
              onChange={handleChange}
              rows="4"
              placeholder="Inserisci eventuali note per la visita"
            />
          </label>

          <div className="actions-row">
            <button
              type="submit"
              className="btn btn-primary"
              disabled={isBookingLoading || isOptionsLoading || isSlotsLoading}
            >
              {isBookingLoading ? 'Prenotazione in corso...' : 'Prenota visita'}
            </button>
            <button
              type="button"
              className="btn btn-outline"
              onClick={() => navigate(backPath)}
              disabled={isBookingLoading}
            >
              Annulla
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
