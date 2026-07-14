import { useState, useEffect } from 'react';
import EmptyMessage from '../../../components/common/EmptyMessage';
import { apiFetch } from '../../../services/apiClient';

export default function ManagmentVisitCategory() {
  const [categorie, setCategorie] = useState([]);
  const [nome, setNome] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Carica le categorie di visita dal backend
  const caricaCategorie = async () => {
    try {
      setLoading(true);
      const response = await apiFetch('/api/categorieVisite/ottieniTutte');
      if (!response.ok) throw new Error('Errore nel caricamento delle categorie di visita');
      const data = await response.json();
      // Filtra solo le categorie non eliminate (soft-delete con isDeleted)
      setCategorie(data.filter(c => !c.isDeleted));
      setError(null);
    } catch (err) {
      setError(err.message);
      setCategorie([]);
    } finally {
      setLoading(false);
    }
  };

  // Carica le categorie al montaggio del componente
  useEffect(() => {
    queueMicrotask(() => {
      caricaCategorie();
    });
  }, []);

  // Aggiunge una nuova categoria di visita
  const handleAggiungiCategoria = async (e) => {
    e.preventDefault();

    if (!nome.trim()) {
      alert('Inserisci il nome della categoria');
      return;
    }

    try {
      const response = await apiFetch('/api/categorieVisite/aggiungi', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          nome: nome.trim(),
          isDeleted: false,
        }),
      });

      if (!response.ok) throw new Error('Errore nel salvataggio della categoria di visita');

      // Resetta il campo del form
      setNome('');
      caricaCategorie();
    } catch (err) {
      alert('Errore: ' + err.message);
    }
  };

  // Elimina una categoria di visita (soft-delete)
  const handleEliminaCategoria = async (id) => {
    if (!window.confirm('Sei sicuro di voler eliminare questa categoria di visita?')) {
      return;
    }

    try {
      const response = await apiFetch(`/api/categorieVisite/elimina/${id}`, {
        method: 'DELETE',
      });

      if (!response.ok) throw new Error('Errore nell\'eliminazione della categoria di visita');

      caricaCategorie();
    } catch (err) {
      alert('Errore: ' + err.message);
    }
  };

  return (
    <div>
      {/* Form di inserimento */}
      <div className="panel section-spaced-sm">
        <h3>Aggiungi Nuova Categoria Visita</h3>
        <form className="stack-form" onSubmit={handleAggiungiCategoria}>
          <label>
            Nome della Categoria
            <input
              type="text"
              className="form-control"
              placeholder="Es: Chirurgia, Controllo, Diagnostica..."
              value={nome}
              onChange={(e) => setNome(e.target.value)}
            />
          </label>

          <button type="submit" className="btn btn-primary align-start">
            Salva Categoria
          </button>
        </form>
      </div>

      {/* Tabella delle categorie */}
      <div className="panel section-spaced-sm">
        <h3>Elenco Categorie Visita</h3>

        {loading ? (
          <p className="muted-text">Caricamento in corso...</p>
        ) : error ? (
          <div style={{ color: '#d32f2f', padding: '10px', borderRadius: '4px', backgroundColor: '#ffebee' }}>
            Errore: {error}
          </div>
        ) : categorie.length > 0 ? (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nome Categoria</th>
                  <th>Azioni</th>
                </tr>
              </thead>
              <tbody>
                {categorie.map((c) => (
                  <tr key={c.id}>
                    <td>{c.id}</td>
                    <td><strong>{c.nome}</strong></td>
                    <td>
                      <button
                        className="btn btn-outline btn-sm"
                        onClick={() => handleEliminaCategoria(c.id)}
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
          <EmptyMessage>Nessuna categoria di visita trovata. Aggiungine una usando il form sopra.</EmptyMessage>
        )}
      </div>
    </div>
  );
}
