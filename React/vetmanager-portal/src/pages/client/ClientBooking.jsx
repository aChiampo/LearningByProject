import { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppContext } from '../../context/AppContext';
import { ROLE_CONFIG } from '../../data/roleConfig';
import PageTitle from '../../components/common/PageTitle';
import { apiFetch, readApiError } from '../../services/apiClient';

export default function ClientBooking() {
  const navigate = useNavigate();
  const { currentRole } = useContext(AppContext);
  const config = ROLE_CONFIG[currentRole];

  // State for form fields
  const [formData, setFormData] = useState({
    animale: '', // Now matches `animale` in VisitaDto
    tipoVisita: '', // Now matches `tipoVisita` in VisitaDto
    veterinario: '',
    data: '',
    fasciaOraria: '',
    pagamento: null, // Add payment if needed
    note: '', // Add notes if needed
  });

  // State for loading and error
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  // Handle input changes
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

    // Guard against non-numeric / missing selections before hitting the API.
    // VisitaDto marks animale, tipoVisita, and veterinario as @NotNull,
    // so a NaN id here would serialize to `null` and fail backend validation
    // with a less helpful error than we can give here.
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
        animale: animaleId,
        tipoVisita: tipoVisitaId,
        veterinario: veterinarioId,
        data: formData.data, // ISO string (YYYY-MM-DD), maps to LocalDate
        fasciaOraria: formData.fasciaOraria,
        pagamento: formData.pagamento ? { id: parseInt(formData.pagamento, 10) } : null,
        note: formData.note,
      };

      const response = await apiFetch('/api/visite/prenotazione', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestBody),
      });

      if (!response.ok) {
        const errorMessage = await readApiError(response, 'Failed to submit booking request');
        throw new Error(errorMessage);
      }

      const data = await response.json();
      console.log('Booking request submitted:', data);
      setSuccess(true);
      setTimeout(() => {
        navigate('/client/dashboard');
      }, 1500);
    } catch (err) {
      console.error('Error:', err);
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
              {config?.animals?.length > 0 ? (
                config.animals.map((animale) => (
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
              <option value="1">Vaccino Annuale</option> {/* Assuming IDs for options */}
              <option value="2">Visita di Controllo</option>
              <option value="3">Chirurgia / Intervento</option>
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
              <option value="1">Dott. Camillo Zampetti</option> {/* Assuming IDs for options */}
              <option value="2">Qualsiasi Veterinario dello Studio</option>
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
            <button type="submit" className="btn btn-primary" disabled={isLoading}>
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
