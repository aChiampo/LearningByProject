import { useState, useEffect } from 'react';
import EmptyMessage from '../../../components/common/EmptyMessage';
import { apiFetch, apiFetchWithPayload } from '../../../services/apiClient';

export default function ManagmentRaces() {
  const [razze, setRazze] = useState([]);
  const [nuovaRazza, setNuovaRazza] = useState('');
  const [specieSelezionata, setSpecieSelezionata] = useState('');
  const [specie, setSpecie] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [razzaInModifica, setRazzaInModifica] = useState(null);
  const [nomeRazzaModificato, setNomeRazzaModificato] = useState('');
  const [specieRazzaModificata, setSpecieRazzaModificata] = useState('');

  // Carica le razze dal backend
  const caricaRazze = async () => {
    try {
      setLoading(true);
      const response = await apiFetch('/api/razze');
      if (!response.ok) throw new Error('Errore nel caricamento delle razze');
      const data = await response.json();
      // Filtra solo le razze non eliminate (soft-delete)
      setRazze(data.filter(r => !r.deleted));
      setError(null);
    } catch (err) {
      setError(err.message);
      setRazze([]);
    } finally {
      setLoading(false);
    }
  };

  // Carica le specie dal backend
  const caricaSpecie = async () => {
    try {
      const response = await apiFetch('/api/specie');
      if (!response.ok) throw new Error('Errore nel caricamento delle specie');
      const data = await response.json();
      setSpecie(data.filter(s => !s.deleted));
    } catch (err) {
      console.error('Errore nel caricamento delle specie:', err);
    }
  };

  // Carica razze e specie al montaggio del componente
  useEffect(() => {
    queueMicrotask(() => {
      caricaRazze();
      caricaSpecie();
    });
  }, []);

  // Aggiunge una nuova razza
  const handleAggiungiRazza = async (e) => {
    e.preventDefault();
    
    if (!nuovaRazza.trim()) {
      alert('Inserisci il nome della razza');
      return;
    }

    if (!specieSelezionata) {
      alert('Seleziona una specie');
      return;
    }

    try {
      const specieObj = specie.find(s => s.id === parseInt(specieSelezionata));
      if (!specieObj) throw new Error('Specie non trovata');

      const response = await apiFetchWithPayload('/api/razze/aggiungiRazza', {
        nome: nuovaRazza,
        idSpecie: {
          id: specieObj.id,
          isDeleted: false
        }
      });

      if (!response.ok) throw new Error('Errore nel salvataggio della razza');
      
      setNuovaRazza('');
      setSpecieSelezionata('');
      caricaRazze();
    } catch (err) {
      alert('Errore: ' + err.message);
    }
  };

  // Apre il modal di modifica
  const handleAperturModifica = (razza) => {
    setRazzaInModifica(razza.id);
    setNomeRazzaModificato(razza.Nome);
    setSpecieRazzaModificata(razza.idSpecie.id.toString());
  };

  // Chiude il modal di modifica
  const handleChiudiModifica = () => {
    setRazzaInModifica(null);
    setNomeRazzaModificato('');
    setSpecieRazzaModificata('');
  };

  // Salva le modifiche
  const handleSalvaModifica = async (e) => {
    e.preventDefault();

    if (!nomeRazzaModificato.trim()) {
      alert('Inserisci il nome della razza');
      return;
    }

    if (!specieRazzaModificata) {
      alert('Seleziona una specie');
      return;
    }

    try {
      const specieObj = specie.find(s => s.id === parseInt(specieSelezionata));
      if (!specieObj) throw new Error('Specie non trovata');

      const response = await apiFetchWithPayload(`/api/razze/${razzaInModifica}`, [
        {
          nome: nomeRazzaModificato,
          idSpecie: {
            id: specieObj.id,
            isDeleted: false
          }
        }
      ], { method: 'PUT' });

      if (!response.ok) throw new Error('Errore nel salvataggio della razza');
      
      handleChiudiModifica();
      caricaRazze();
    } catch (err) {
      alert('Errore: ' + err.message);
    }
  };

  // Elimina una razza (soft-delete)
  const handleEliminaRazza = async (id) => {
    if (!window.confirm('Sei sicuro di voler eliminare questa razza?')) {
      return;
    }

    try {
      const response = await apiFetch(`/api/razze/${id}`, {
        method: 'DELETE',
      });

      if (!response.ok) throw new Error('Errore nell\'eliminazione della razza');
      
      caricaRazze();
    } catch (err) {
      alert('Errore: ' + err.message);
    }
  };

  return (
    <div>
      {/* Form di inserimento */}
      <div className="panel section-spaced-sm">
        <h3>Aggiungi Nuova Razza</h3>
        <form className="stack-form" onSubmit={handleAggiungiRazza}>
          <label>
            Specie
            <select
              className="form-control"
              value={specieSelezionata}
              onChange={(e) => setSpecieSelezionata(e.target.value)}
            >
              <option value="">-- Seleziona una specie --</option>
              {specie.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.Nome}
                </option>
              ))}
            </select>
          </label>
          <label>
            Nome Razza
            <input
              type="text"
              className="form-control"
              placeholder="Es: Labrador, Persiano, Belga..."
              value={nuovaRazza}
              onChange={(e) => setNuovaRazza(e.target.value)}
            />
          </label>
          <button type="submit" className="btn btn-primary align-start">
            Salva Razza
          </button>
        </form>
      </div>

      {/* Tabella delle razze */}
      <div className="panel section-spaced-sm">
        <h3>Elenco Razze</h3>
        
        {loading ? (
          <p className="muted-text">Caricamento in corso...</p>
        ) : error ? (
          <div style={{ color: '#d32f2f', padding: '10px', borderRadius: '4px', backgroundColor: '#ffebee' }}>
            Errore: {error}
          </div>
        ) : razze.length > 0 ? (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nome Razza</th>
                  <th>Specie</th>
                  <th>Azioni</th>
                </tr>
              </thead>
              <tbody>
                {razze.map((r) => (
                  <tr key={r.id}>
                    <td>{r.id}</td>
                    <td><strong>{r.Nome}</strong></td>
                    <td>{r.idSpecie.Nome || 'N/A'}</td>
                    <td>
                      <button
                        className="btn btn-outline btn-sm"
                        onClick={() => handleAperturModifica(r)}
                      >
                        Modifica
                      </button>
                      <button
                        className="btn btn-outline btn-sm"
                        onClick={() => handleEliminaRazza(r.id)}
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
          <EmptyMessage>Nessuna razza trovata. Aggiungine una usando il form sopra.</EmptyMessage>
        )}
      </div>

      {/* Modal di modifica */}
      {razzaInModifica && (
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
            <h3>Modifica Razza</h3>
            <form className="stack-form" onSubmit={handleSalvaModifica}>
              <label>
                Specie
                <select
                  className="form-control"
                  value={specieRazzaModificata}
                  onChange={(e) => setSpecieRazzaModificata(e.target.value)}
                >
                  <option value="">-- Seleziona una specie --</option>
                  {specie.map((s) => (
                    <option key={s.id} value={s.id}>
                      {s.Nome}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Nome Razza
                <input
                  type="text"
                  className="form-control"
                  value={nomeRazzaModificato}
                  onChange={(e) => setNomeRazzaModificato(e.target.value)}
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
