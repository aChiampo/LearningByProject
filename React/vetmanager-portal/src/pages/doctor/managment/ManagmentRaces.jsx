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

      const response = await apiFetchWithPayload('/api/razze', [
        {
          nome: nuovaRazza,
          idSpecie: specieObj
        }
      ]);

      if (!response.ok) throw new Error('Errore nel salvataggio della razza');
      
      setNuovaRazza('');
      setSpecieSelezionata('');
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
    </div>
  );
}
