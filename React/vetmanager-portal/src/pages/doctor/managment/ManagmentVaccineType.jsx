import { useState, useEffect } from 'react';
import EmptyMessage from '../../../components/common/EmptyMessage';

export default function ManagmentVaccineType() {
  const [vaccini, setVaccini] = useState([]);
  const [tipologia, setTipologia] = useState('');
  const [durata, setDurata] = useState('');
  const [note, setNote] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Carica i tipi di vaccino dal backend
  const caricaVaccini = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:9020/api/tipi-vaccino');
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
    caricaVaccini();
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
      const response = await fetch('http://localhost:9020/api/tipi-vaccino', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          tipologia: tipologia.trim(),
          durata: parseInt(durata),
          note: note.trim() || null,
          isDeleted: false,
        }),
      });

      if (!response.ok) throw new Error('Errore nel salvataggio del tipo di vaccino');

      // Resetta i campi del form
      setTipologia('');
      setDurata('');
      setNote('');
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
      const response = await fetch(`http://localhost:9020/api/tipi-vaccino/${id}`, {
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
    </div>
  );
}
