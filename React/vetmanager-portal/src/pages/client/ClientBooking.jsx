import { useEffect, useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppContext } from '../../context/AppContext';
import PageTitle from '../../components/common/PageTitle';
import { apiFetchWithPayload, readApiError } from '../../services/apiClient';
import { fetchAnimalsByOwner } from '../../services/animalApi';
import { fetchDoctors } from '../../services/userApi';
import { fetchVisitTypes } from '../../services/visitTypeApi';

export default function ClientBooking() {
  const navigate = useNavigate();
  const { currentUser } = useContext(AppContext);
  const ownerId = currentUser?.id;

  const [formData, setFormData] = useState({
    animale: '',
    tipoVisita: '',
    veterinario: '',
    data: '',
    fasciaOraria: '',
    pagamento: null,
    note: '',
  });

  const [animals, setAnimals] = useState([]);
  const [visitTypes, setVisitTypes] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [isOptionsLoading, setIsOptionsLoading] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    if (!ownerId) return;

    let isMounted = true;

    async function loadOptions() {
      setIsOptionsLoading(true);
      setError(null);

      try {
        const [animalList, visitTypeList, doctorList] = await Promise.all([
          fetchAnimalsByOwner(ownerId),
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
  }, [ownerId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  // Handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(false);

    const animaleId = parseInt(formData.animale, 10);
    const tipoVisitaId = parseInt(formData.tipoVisita, 10);
    const veterinarioId = parseInt(formData.veterinario, 10);

    if (Number.isNaN(animaleId)) {
      setError('Seleziona un animale valido prima di continuare.');
      return;
    }
    if (Number.isNaN(tipoVisitaId)) {
      setError('Seleziona un tipo di prestazione valido.');
      return;
    }
    if (Number.isNaN(veterinarioId)) {
      setError('Seleziona un veterinario valido.');
      return;
    }
    if (!formData.data) {
      setError('Seleziona una data per la visita.');
      return;
    }
    if (!formData.fasciaOraria) {
      setError('Seleziona una fascia oraria.');
      return;
    }

    setIsLoading(true);

    try {
      const requestBody = {
        animale: { id: animaleId },
        tipoVisita: { id: tipoVisitaId },
        veterinario: { id: veterinarioId },
        data: formData.data,
        fasciaOraria: formData.fasciaOraria,
        pagamento: formData.pagamento ? { id: parseInt(formData.pagamento, 10) } : null,
        note: formData.note,
      };

      const response = await apiFetchWithPayload('/api/visite/prenotazione', [requestBody]);

      if (!response.ok) {
        const errorMessage = await readApiError(response, 'Failed to submit booking request');
        throw new Error(errorMessage);
      }

      await response.json();
      setSuccess(true);
      setTimeout(() => {
        navigate('/client/dashboard');
      }, 1500);
    } catch (err) {
      setError(err.message || 'An error occurred while submitting the request.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div>
      <PageTitle eyebrow="Nuova Richiesta" title="Prenota Appuntamento" />
      <div className="panel panel-narrow">
        <form className="stack-form" onSubmit={handleSubmit}>
          {isOptionsLoading && <p className="muted-text">Caricamento opzioni...</p>}
          {error && <p style={{ color: 'red' }}>{error}</p>}
          {success && <p style={{ color: 'green' }}>Richiesta inviata con successo!</p>}

          <label>
            Seleziona l'animale
            <select
              name="animale"
              value={formData.animale}
              onChange={handleChange}
              required
            >
              <option value="">Seleziona un animale</option>
              {animals.length > 0 ? (
                animals.map((animale) => (
                  <option key={animale.id} value={animale.id}>
                    {animale.nome} ({animale.specie})
                  </option>
                ))
              ) : (
                <option value="" disabled>
                  Nessun animale salvato - contatta la clinica
                </option>
              )}
            </select>
          </label>

          <label>
            Tipo di prestazione
            <select
              name="tipoVisita"
              value={formData.tipoVisita}
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
            Veterinario preferito
            <select
              name="veterinario"
              value={formData.veterinario}
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
            Data e Fascia Oraria
            <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
              <input
                type="date"
                name="data"
                value={formData.data}
                onChange={handleChange}
                required
              />
              <select
                name="fasciaOraria"
                value={formData.fasciaOraria}
                onChange={handleChange}
                className="field-gap"
                required
              >
                <option value="">Seleziona fascia oraria</option>
                <option value="Mattina (09:00 - 12:30)">Mattina (09:00 - 12:30)</option>
                <option value="Pomeriggio (14:30 - 18:30)">Pomeriggio (14:30 - 18:30)</option>
              </select>
            </div>
          </label>

          <div className="actions-row">
            <button type="submit" className="btn btn-primary" disabled={isLoading || isOptionsLoading}>
              {isLoading ? 'Invio in corso...' : 'Invia Richiesta'}
            </button>
            <button
              type="button"
              className="btn btn-outline"
              onClick={() => navigate('/client/dashboard')}
              disabled={isLoading}
            >
              Annulla
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
