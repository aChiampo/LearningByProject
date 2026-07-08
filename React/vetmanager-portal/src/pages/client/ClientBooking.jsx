import { useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppContext } from '../../context/AppContext';
import { ROLE_CONFIG } from '../../data/roleConfig';
import PageTitle from '../../components/common/PageTitle';

export default function ClientBooking() {
  const navigate = useNavigate();
  const { currentRole } = useContext(AppContext);
  const config = ROLE_CONFIG[currentRole];

  function handleSubmit(event) {
    event.preventDefault();
    window.alert('Richiesta inviata con successo!');
    navigate('/client/dashboard');
  }

  return (
    <div>
      <PageTitle eyebrow="Nuova Richiesta" title="Prenota Appuntamento" />

      <div className="panel panel-narrow">
        <form className="stack-form" onSubmit={handleSubmit}>
          <label>
            Seleziona l'animale
            <select>
              {config?.animals?.length > 0 ? (
                config.animals.map((animale, index) => (
                  <option key={index}>{animale.nome} ({animale.specie})</option>
                ))
              ) : (
                <option>Nessun animale salvato - Inserimento manuale</option>
              )}
            </select>
          </label>

          <label>
            Tipo di prestazione
            <select>
              <option>Vaccino Annuale</option>
              <option>Visita di Controllo</option>
              <option>Chirurgia / Intervento</option>
            </select>
          </label>

          <label>
            Veterinario preferito
            <select>
              <option>Dott. Camillo Zampetti</option>
              <option>Qualsiasi Veterinario dello Studio</option>
            </select>
          </label>

          <label>
            Data e Fascia Oraria
            <input type="date" />
            <select className="field-gap">
              <option>Mattina (09:00 - 12:30)</option>
              <option>Pomeriggio (14:30 - 18:30)</option>
            </select>
          </label>

          <div className="actions-row">
            <button type="submit" className="btn btn-primary">Invia Richiesta</button>
            <button type="button" className="btn btn-outline" onClick={() => navigate('/client/dashboard')}>
              Annulla
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
