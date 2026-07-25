import { useState, useEffect } from 'react';
import EmptyMessage from '../../../components/common/EmptyMessage';
import { apiFetch, apiFetchWithPayload, readApiError } from '../../../services/apiClient';

function getEntityName(entity) {
  return entity?.nome ?? entity?.Nome ?? '';
}

function isActiveEntity(entity) {
  return !entity?.deleted && !entity?.isDeleted;
}

export default function ManagmentSpecies() {
  const [specie, setSpecie] = useState([]);
  const [nuomaSpecie, setNuomaSpecie] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [specieInModifica, setSpecieInModifica] = useState(null);
  const [nomeSpecieModificato, setNomeSpecieModificato] = useState('');

  // Carica le specie dal backend
  const caricaSpecie = async () => {
    try {
      setLoading(true);
      const response = await apiFetch('/api/specie');
      if (!response.ok) throw new Error('Errore nel caricamento delle specie');
      const data = await response.json();
      // Filtra solo le specie non eliminate (soft-delete)
      setSpecie(data.filter(isActiveEntity));
      setError(null);
    } catch (err) {
      setError(err.message);
      setSpecie([]);
    } finally {
      setLoading(false);
    }
  };

  // Carica le specie al montaggio del componente
  useEffect(() => {
    queueMicrotask(() => {
      caricaSpecie();
    });
  }, []);

  // Aggiunge una nuova specie
  const handleAggiungiSpecie = async (e) => {
    e.preventDefault();
    
    if (!nuomaSpecie.trim()) {
      alert('Inserisci il nome della specie');
      return;
    }

    try {
      const response = await apiFetchWithPayload('/api/specie', {
        nome: nuomaSpecie.trim()
      });

      if (!response.ok) {
        throw new Error(await readApiError(response, 'Errore nel salvataggio della specie'));
      }
      
      setNuomaSpecie('');
      caricaSpecie();
    } catch (err) {
      alert('Errore: ' + err.message);
    }
  };

  // Apre il modal di modifica
  const handleAperturModifica = (species) => {
    setSpecieInModifica(species.id);
    setNomeSpecieModificato(getEntityName(species));
  };

  // Chiude il modal di modifica
  const handleChiudiModifica = () => {
    setSpecieInModifica(null);
    setNomeSpecieModificato('');
  };

  // Salva le modifiche
  const handleSalvaModifica = async (e) => {
    e.preventDefault();

    if (!nomeSpecieModificato.trim()) {
      alert('Inserisci il nome della specie');
      return;
    }

    try {
      const response = await apiFetchWithPayload(`/api/specie/${specieInModifica}`, {
        nome: nomeSpecieModificato.trim()
      }, { method: 'PUT' });

      if (!response.ok) {
        throw new Error(await readApiError(response, 'Errore nel salvataggio della specie'));
      }
      
      handleChiudiModifica();
      caricaSpecie();
    } catch (err) {
      alert('Errore: ' + err.message);
    }
  };

  // Elimina una specie (soft-delete)
  const handleEliminaSpecie = async (id) => {
    if (!window.confirm('Sei sicuro di voler eliminare questa specie?')) {
      return;
    }

    try {
      const response = await apiFetch(`/api/specie/${id}`, {
        method: 'DELETE',
      });

      if (!response.ok) throw new Error('Errore nell\'eliminazione della specie');
      
      caricaSpecie();
    } catch (err) {
      alert('Errore: ' + err.message);
    }
  };

  return (
    <div>
      {/* Form di inserimento */}
      <div className="panel section-spaced-sm">
        <h3>Aggiungi Nuova Specie</h3>
        <form className="stack-form" onSubmit={handleAggiungiSpecie}>
          <label>
            Nome Specie
            <input
              type="text"
              className="form-control"
              placeholder="Es: Cane, Gatto, Coniglio..."
              value={nuomaSpecie}
              onChange={(e) => setNuomaSpecie(e.target.value)}
            />
          </label>
          <button type="submit" className="btn btn-primary align-start">
            Salva Specie
          </button>
        </form>
      </div>

      {/* Tabella delle specie */}
      <div className="panel section-spaced-sm">
        <h3>Elenco Specie</h3>
        
        {loading ? (
          <p className="muted-text">Caricamento in corso...</p>
        ) : error ? (
          <div style={{ color: '#d32f2f', padding: '10px', borderRadius: '4px', backgroundColor: '#ffebee' }}>
            Errore: {error}
          </div>
        ) : specie.length > 0 ? (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nome Specie</th>
                  <th>Azioni</th>
                </tr>
              </thead>
              <tbody>
                {specie.map((s) => (
                  <tr key={s.id}>
                    <td>{s.id}</td>
                    <td><strong>{getEntityName(s)}</strong></td>
                    <td>
                      <button
                        className="btn btn-outline btn-sm"
                        onClick={() => handleAperturModifica(s)}
                      >
                        Modifica
                      </button>
                      <button
                        className="btn btn-outline btn-sm"
                        onClick={() => handleEliminaSpecie(s.id)}
                      >
                        Elimina
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <EmptyMessage>Nessuna specie trovata. Aggiungine una usando il form sopra.</EmptyMessage>
        )}
      </div>

      {/* Modal di modifica */}
      {specieInModifica && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(0, 0, 0, 0.5)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 1000
        }}>
          <div style={{
            backgroundColor: 'white',
            padding: '20px',
            borderRadius: '8px',
            maxWidth: '500px',
            width: '90%'
          }}>
            <h3>Modifica Specie</h3>
            <form className="stack-form" onSubmit={handleSalvaModifica}>
              <label>
                Nome Specie
                <input
                  type="text"
                  className="form-control"
                  value={nomeSpecieModificato}
                  onChange={(e) => setNomeSpecieModificato(e.target.value)}
                />
              </label>
              <div style={{ display: 'flex', gap: '10px' }}>
                <button type="submit" className="btn btn-primary">
                  Salva
                </button>
                <button type="button" className="btn btn-outline" onClick={handleChiudiModifica}>
                  Annulla
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
