import { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppContext } from '../../context/AppContext';
import { ROLE_CONFIG } from '../../data/roleConfig';
import PageTitle from '../../components/common/PageTitle';

export default function ClientBooking() {
  const navigate = useNavigate();
  const { currentRole } = useContext(AppContext);
  const config = ROLE_CONFIG[currentRole];

  // State for form fields
  const [formData, setFormData] = useState({
    animaleId: '',
    tipoPrestazione: '',
    veterinario: '',
    data: '',
    fasciaOraria: '',
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
    setIsLoading(true);
    setError(null);
    setSuccess(false);

    try {
      const response = await fetch('http://localhost:9020/api/prenotazione', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          idAnimale: formData.animaleId,
          tipoPrestazione: formData.tipoPrestazione,
          veterinario: formData.veterinario,
          data: formData.data,
          fasciaOraria: formData.fasciaOraria,
        }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to submit booking request');
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
              name="animaleId"
              value={formData.animaleId}
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
                <option value="manual">Nessun animale salvato - Inserimento manuale</option>
              )}
            </select>
          </label>

          <label>
            Tipo di prestazione
            <select
              name="tipoPrestazione"
              value={formData.tipoPrestazione}
              onChange={handleChange}
              required
            >
              <option value="">Seleziona un tipo</option>
              <option value="Vaccino Annuale">Vaccino Annuale</option>
              <option value="Visita di Controllo">Visita di Controllo</option>
              <option value="Chirurgia / Intervento">Chirurgia / Intervento</option>
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
              <option value="Dott. Camillo Zampetti">Dott. Camillo Zampetti</option>
              <option value="Qualsiasi Veterinario dello Studio">Qualsiasi Veterinario dello Studio</option>
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