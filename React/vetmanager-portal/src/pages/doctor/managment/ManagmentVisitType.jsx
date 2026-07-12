import { useState, useEffect } from 'react';
import EmptyMessage from '../../../components/common/EmptyMessage';

export default function ManagmentVisitType() {
  const [visite, setVisite] = useState([]);
  const [nome, setNome] = useState('');
  const [durata, setDurata] = useState('');
  const [prezzo, setPrezzo] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Carica i tipi di visita dal backend
  const caricaVisite = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:9020/api/tipiVisite/ottieniTutti');
      if (!response.ok) throw new Error('Errore nel caricamento dei tipi di visita');
      const data = await response.json();
      // Filtra solo le visite non eliminate (soft-delete con isDeleted)
      setVisite(data.filter(v => !v.isDeleted));
      setError(null);
    } catch (err) {
      setError(err.message);
      setVisite([]);
    } finally {
      setLoading(false);
    }
  };

  // Carica le visite al montaggio del componente
  useEffect(() => {
    caricaVisite();
  }, []);

  // Aggiunge un nuovo tipo di visita
  const handleAggiungiVisita = async (e) => {
    e.preventDefault();

    if (!nome.trim()) {
      alert('Inserisci il nome della visita');
      return;
    }

    if (!durata || durata <= 0) {
      alert('Inserisci una durata valida (maggiore di 0)');
      return;
    }

    if (prezzo === '' || prezzo < 0) {
      alert('Inserisci un prezzo valido (maggiore o uguale a 0)');
      return;
    }

    try {
      const response = await fetch('http://localhost:9020/api/tipiVisite/aggiungi', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          nome: nome.trim(),
          durata: parseInt(durata),
          prezzo: parseFloat(prezzo),
          isDeleted: false,
          attivo: true,
        }),
      });

      if (!response.ok) throw new Error('Errore nel salvataggio del tipo di visita');

      // Resetta i campi del form
      setNome('');
      setDurata('');
      setPrezzo('');
      caricaVisite();
    } catch (err) {
      alert('Errore: ' + err.message);
    }
  };

  // Elimina un tipo di visita (soft-delete)
  const handleEliminaVisita = async (id) => {
    if (!window.confirm('Sei sicuro di voler eliminare questo tipo di visita?')) {
      return;
    }

    try {
      const response = await fetch(`http://localhost:9020/api/tipiVisite/elimina/${id}`, {
        method: 'DELETE',
      });

      if (!response.ok) throw new Error('Errore nell\'eliminazione del tipo di visita');

      caricaVisite();
    } catch (err) {
      alert('Errore: ' + err.message);
    }
  };

  return (
    <div>
      {/* Form di inserimento */}
      <div className="panel section-spaced-sm">
        <h3>Aggiungi Nuovo Tipo di Visita</h3>
        <form className="stack-form" onSubmit={handleAggiungiVisita}>
          <label>
            Nome della Visita
            <input
              type="text"
              className="form-control"
              placeholder="Es: Visita di Controllo, Chirurgia Generale..."
              value={nome}
              onChange={(e) => setNome(e.target.value)}
            />
          </label>

          <label>
            Durata in Minuti
            <input
              type="number"
              className="form-control"
              placeholder="Es: 30"
              min="1"
              value={durata}
              onChange={(e) => setDurata(e.target.value)}
            />
          </label>

          <label>
            Prezzo (€)
            <input
              type="number"
              className="form-control"
              placeholder="Es: 50.00"
              min="0"
              step="0.01"
              value={prezzo}
              onChange={(e) => setPrezzo(e.target.value)}
            />
          </label>

          <button type="submit" className="btn btn-primary align-start">
            Salva Tipo Visita
          </button>
        </form>
      </div>

      {/* Tabella dei tipi di visita */}
      <div className="panel section-spaced-sm">
        <h3>Elenco Tipi di Visita</h3>

        {loading ? (
          <p className="muted-text">Caricamento in corso...</p>
        ) : error ? (
          <div style={{ color: '#d32f2f', padding: '10px', borderRadius: '4px', backgroundColor: '#ffebee' }}>
            Errore: {error}
          </div>
        ) : visite.length > 0 ? (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nome Visita</th>
                  <th>Durata (Minuti)</th>
                  <th>Prezzo (€)</th>
                  <th>Azioni</th>
                </tr>
              </thead>
              <tbody>
                {visite.map((v) => (
                  <tr key={v.id}>
                    <td>{v.id}</td>
                    <td><strong>{v.nome}</strong></td>
                    <td>{v.durata}</td>
                    <td>{typeof v.prezzo === 'number' ? v.prezzo.toFixed(2) : '—'}</td>
                    <td>
                      <button
                        className="btn btn-outline btn-sm"
                        onClick={() => handleEliminaVisita(v.id)}
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
          <EmptyMessage>Nessun tipo di visita trovato. Aggiungine uno usando il form sopra.</EmptyMessage>
        )}
      </div>
    </div>
  );
}
