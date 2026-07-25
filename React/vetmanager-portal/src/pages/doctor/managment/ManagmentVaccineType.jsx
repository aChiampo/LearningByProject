import { useState, useEffect } from 'react';
import EmptyMessage from '../../../components/common/EmptyMessage';
import { apiFetch, apiFetchWithPayload, readApiError } from '../../../services/apiClient';

export default function ManagmentVaccineType() {
  const [vaccini, setVaccini] = useState([]);
  const [tipologia, setTipologia] = useState('');
  const [durata, setDurata] = useState('');
  const [note, setNote] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [vaccinoInModifica, setVaccinoInModifica] = useState(null);
  const [tipologiaModificata, setTipologiaModificata] = useState('');
  const [durataModificata, setDurataModificata] = useState('');
  const [noteModificate, setNoteModificate] = useState('');

  // Carica i tipi di vaccino dal backend
  const caricaVaccini = async () => {
    try {
      setLoading(true);
      const response = await apiFetch('/api/tipi-vaccino');
      if (!response.ok) throw new Error('Errore nel caricamento dei tipi di vaccino');
      const data = await response.json();
      // Filtra solo i vaccini non eliminati (soft-delete con isDeleted)
      setVaccini(data.filter(v => !v.isDeleted));
      setError(null);
    } catch (err) {
      setError(err.message);
      setVaccini([]);
    } finally {
      setLoading(false);
    }
  };

  // Carica i vaccini al montaggio del componente
  useEffect(() => {
    queueMicrotask(() => {
      caricaVaccini();
    });
  }, []);

  // Aggiunge un nuovo tipo di vaccino
  const handleAggiungiVaccino = async (e) => {
    e.preventDefault();

    if (!tipologia.trim()) {
      alert('Inserisci la tipologia del vaccino');
      return;
    }

    if (!durata || durata <= 0) {
      alert('Inserisci una durata valida (maggiore di 0)');
      return;
    }

    try {
      const response = await apiFetchWithPayload('/api/tipi-vaccino', {
        tipologia: tipologia.trim(),
        durata: parseInt(durata, 10),
        note: note.trim() || null,
      });

      if (!response.ok) {
        throw new Error(await readApiError(response, 'Errore nel salvataggio del tipo di vaccino'));
      }

      // Resetta i campi del form
      setTipologia('');
      setDurata('');
      setNote('');
      caricaVaccini();
    } catch (err) {
      alert('Errore: ' + err.message);
    }
  };

  // Apre il modal di modifica
  const handleAperturModifica = (vaccino) => {
    setVaccinoInModifica(vaccino.id);
    setTipologiaModificata(vaccino.tipologia);
    setDurataModificata(vaccino.durata.toString());
    setNoteModificate(vaccino.note || '');
  };

  // Chiude il modal di modifica
  const handleChiudiModifica = () => {
    setVaccinoInModifica(null);
    setTipologiaModificata('');
    setDurataModificata('');
    setNoteModificate('');
  };

  // Salva le modifiche
  const handleSalvaModifica = async (e) => {
    e.preventDefault();

    if (!tipologiaModificata.trim()) {
      alert('Inserisci la tipologia del vaccino');
      return;
    }

    if (!durataModificata || durataModificata <= 0) {
      alert('Inserisci una durata valida (maggiore di 0)');
      return;
    }

    try {
      const response = await apiFetchWithPayload(`/api/tipi-vaccino/${vaccinoInModifica}`, {
        tipologia: tipologiaModificata.trim(),
        durata: parseInt(durataModificata, 10),
        note: noteModificate.trim() || null,
      }, { method: 'PUT' });

      if (!response.ok) {
        throw new Error(await readApiError(response, 'Errore nel salvataggio del tipo di vaccino'));
      }
      
      handleChiudiModifica();
      caricaVaccini();
    } catch (err) {
      alert('Errore: ' + err.message);
    }
  };

  // Elimina un tipo di vaccino (soft-delete)
  const handleEliminaVaccino = async (id) => {
    if (!window.confirm('Sei sicuro di voler eliminare questo tipo di vaccino?')) {
      return;
    }

    try {
      const response = await apiFetch(`/api/tipi-vaccino/${id}`, {
        method: 'DELETE',
      });

      if (!response.ok) throw new Error('Errore nell\'eliminazione del tipo di vaccino');

      caricaVaccini();
    } catch (err) {
      alert('Errore: ' + err.message);
    }
  };

  return (
    <div>
      {/* Form di inserimento */}
      <div className="panel section-spaced-sm">
        <h3>Aggiungi Nuovo Tipo di Vaccino</h3>
        <form className="stack-form" onSubmit={handleAggiungiVaccino}>
          <label>
            Tipologia
            <input
              type="text"
              className="form-control"
              placeholder="Es: Pentavalente, Antirabbica..."
              value={tipologia}
              onChange={(e) => setTipologia(e.target.value)}
            />
          </label>

          <label>
            Durata in Mesi
            <input
              type="number"
              className="form-control"
              placeholder="Es: 12"
              min="1"
              value={durata}
              onChange={(e) => setDurata(e.target.value)}
            />
          </label>

          <label>
            Note
            <textarea
              className="form-control"
              placeholder="Aggiungi eventuali note (opzionale)"
              value={note}
              onChange={(e) => setNote(e.target.value)}
              rows="3"
            />
          </label>

          <button type="submit" className="btn btn-primary align-start">
            Salva Tipo Vaccino
          </button>
        </form>
      </div>

      {/* Tabella dei tipi di vaccino */}
      <div className="panel section-spaced-sm">
        <h3>Elenco Tipi di Vaccino</h3>

        {loading ? (
          <p className="muted-text">Caricamento in corso...</p>
        ) : error ? (
          <div style={{ color: '#d32f2f', padding: '10px', borderRadius: '4px', backgroundColor: '#ffebee' }}>
            Errore: {error}
          </div>
        ) : vaccini.length > 0 ? (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Tipologia</th>
                  <th>Durata (Mesi)</th>
                  <th>Note</th>
                  <th>Azioni</th>
                </tr>
              </thead>
              <tbody>
                {vaccini.map((v) => (
                  <tr key={v.id}>
                    <td>{v.id}</td>
                    <td><strong>{v.tipologia}</strong></td>
                    <td>{v.durata}</td>
                    <td>{v.note || '—'}</td>
                    <td>
                      <button
                        className="btn btn-outline btn-sm"
                        onClick={() => handleAperturModifica(v)}
                      >
                        Modifica
                      </button>
                      <button
                        className="btn btn-outline btn-sm"
                        onClick={() => handleEliminaVaccino(v.id)}
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
          <EmptyMessage>Nessun tipo di vaccino trovato. Aggiungine uno usando il form sopra.</EmptyMessage>
        )}
      </div>

      {/* Modal di modifica */}
      {vaccinoInModifica && (
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
            <h3>Modifica Tipo di Vaccino</h3>
            <form className="stack-form" onSubmit={handleSalvaModifica}>
              <label>
                Tipologia
                <input
                  type="text"
                  className="form-control"
                  value={tipologiaModificata}
                  onChange={(e) => setTipologiaModificata(e.target.value)}
                />
              </label>

              <label>
                Durata in Mesi
                <input
                  type="number"
                  className="form-control"
                  min="1"
                  value={durataModificata}
                  onChange={(e) => setDurataModificata(e.target.value)}
                />
              </label>

              <label>
                Note
                <textarea
                  className="form-control"
                  value={noteModificate}
                  onChange={(e) => setNoteModificate(e.target.value)}
                  rows="3"
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
