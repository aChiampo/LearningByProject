import { useState, useEffect } from 'react';
import EmptyMessage from '../../../components/common/EmptyMessage';
import { apiFetch, apiFetchWithPayload, readApiError } from '../../../services/apiClient';

function getEntityName(entity) {
  if (!entity) return '';
  if (typeof entity === 'string') return entity;
  return entity.nome ?? entity.Nome ?? entity.tipologia ?? entity.Tipologia ?? '';
}

function isActiveEntity(entity) {
  return !entity?.deleted && !entity?.isDeleted;
}

export default function ManagementVaccinazioni() {
  // Stati per liste dati
  const [vaccinazioni, setVaccinazioni] = useState([]);
  const [animali, setAnimali] = useState([]);
  const [tipiVaccino, setTipiVaccino] = useState([]);

  // Stati Form Inserimento
  const [animaleId, setAnimaleId] = useState('');
  const [tipoVaccinoId, setTipoVaccinoId] = useState('');
  const [dataVaccinazione, setDataVaccinazione] = useState('');
  const [lotto, setLotto] = useState('');

  // UI States
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Modal Modifica
  const [vaccinazioneInModifica, setVaccinazioneInModifica] = useState(null);
  const [animaleIdMod, setAnimaleIdMod] = useState('');
  const [tipoVaccinoIdMod, setTipoVaccinoIdMod] = useState('');
  const [dataVaccinazioneMod, setDataVaccinazioneMod] = useState('');
  const [lottoMod, setLottoMod] = useState('');

  // Carica elenco vaccinazioni
  const caricaVaccinazioni = async () => {
    try {
      setLoading(true);
      const response = await apiFetch('/api/vaccinazioni');
      if (!response.ok) throw new Error('Errore nel caricamento delle vaccinazioni');
      const data = await response.json();

      // Log per ispezionare la struttura esatta dei dati restituiti dal backend
      console.log('Vaccinazioni ricevute dal backend:', data);

      setVaccinazioni(data.filter(isActiveEntity));
      setError(null);
    } catch (err) {
      setError(err.message);
      setVaccinazioni([]);
    } finally {
      setLoading(false);
    }
  };

  // Carica selezioni (Animali e Tipi Vaccino)
  const caricaDatiAccessori = async () => {
    try {
      const resAnim = await apiFetch('/api/animali/leggiTutti');
      if (resAnim.ok) {
        const dataAnim = await resAnim.json();
        setAnimali(dataAnim.filter(isActiveEntity));
      }

      const resTipi = await apiFetch('/api/tipi-vaccino');
      if (resTipi.ok) {
        const dataTipi = await resTipi.json();
        setTipiVaccino(dataTipi.filter(isActiveEntity));
      }
    } catch (err) {
      console.error("Errore nel caricamento dei dati accessori:", err);
    }
  };

  useEffect(() => {
    queueMicrotask(() => {
      caricaVaccinazioni();
      caricaDatiAccessori();
    });
  }, []);

  const formattaLocalDateTime = (dataStr) => {
    if (!dataStr) return null;
    return dataStr.length === 10 ? `${dataStr}T00:00:00` : dataStr;
  };

  // Helper completo per estrarre il nome dell'animale
  const renderNomeAnimale = (v) => {
    if (!v) return '—';
    // Se la risposta è un DTO piatto
    if (v.nomeAnimale) return v.nomeAnimale;
    if (v.animaleNome) return v.animaleNome;
    // Se la risposta contiene l'oggetto Animale annidato o la mappa accessori
    if (v.idAnimale) {
      if (typeof v.idAnimale === 'object') return getEntityName(v.idAnimale);
      const Trovato = animali.find(a => a.id === v.idAnimale);
      if (Trovato) return getEntityName(Trovato);
    }
    if (v.animale) {
      if (typeof v.animale === 'object') return getEntityName(v.animale);
      const Trovato = animali.find(a => a.id === v.animale);
      if (Trovato) return getEntityName(Trovato);
    }
    if (v.animaleId) {
      const trovato = animali.find(a => a.id === v.animaleId);
      if (trovato) return getEntityName(trovato);
    }
    return '—';
  };

  // Helper completo per estrarre il tipo di vaccino
  const renderTipoVaccino = (v) => {
    if (!v) return '—';
    // Se DTO piatto
    if (v.nomeTipoVaccino) return v.nomeTipoVaccino;
    if (v.tipologiaVaccino) return v.tipologiaVaccino;
    if (v.tipoVaccinoNome) return v.tipoVaccinoNome;
    // Se oggetto annidato o riferimento ID
    if (v.idTipoVaccino) {
      if (typeof v.idTipoVaccino === 'object') return v.idTipoVaccino.tipologia || getEntityName(v.idTipoVaccino);
      const trovato = tipiVaccino.find(t => t.id === v.idTipoVaccino);
      if (trovato) return trovato.tipologia || getEntityName(trovato);
    }
    if (v.tipoVaccino) {
      if (typeof v.tipoVaccino === 'object') return v.tipoVaccino.tipologia || getEntityName(v.tipoVaccino);
      const trovato = tipiVaccino.find(t => t.id === v.tipoVaccino);
      if (trovato) return trovato.tipologia || getEntityName(trovato);
    }
    if (v.tipoVaccinoId) {
      const trovato = tipiVaccino.find(t => t.id === v.tipoVaccinoId);
      if (trovato) return trovato.tipologia || getEntityName(trovato);
    }
    return '—';
  };

  // Aggiunge una nuova vaccinazione
  const handleAggiungiVaccinazione = async (e) => {
    e.preventDefault();

    if (!animaleId) {
      alert('Seleziona un animale');
      return;
    }
    if (!tipoVaccinoId) {
      alert('Seleziona il tipo di vaccino');
      return;
    }
    if (!dataVaccinazione) {
      alert('Inserisci la data di somministrazione');
      return;
    }

    try {
      const payload = {
        animaleId: Number(animaleId),
        tipoVaccinoId: Number(tipoVaccinoId),
        dataVaccinazione: formattaLocalDateTime(dataVaccinazione),
        lotto: lotto ? lotto.trim() : ''
      };

      const response = await apiFetchWithPayload('/api/vaccinazioni', payload, {
        method: 'POST'
      });

      if (!response.ok) {
        throw new Error(await readApiError(response, 'Errore nel salvataggio della vaccinazione'));
      }

      setAnimaleId('');
      setTipoVaccinoId('');
      setDataVaccinazione('');
      setLotto('');

      caricaVaccinazioni();
    } catch (err) {
      alert('Errore: ' + err.message);
    }
  };

  // Promemoria
  const handleInviaPromemoria = async (id) => {
    try {
      const response = await apiFetch(`/api/vaccinazioni/${id}/promemoria`, {
        method: 'POST'
      });
      if (!response.ok) throw new Error(await readApiError(response, 'Errore invio promemoria'));

      const messaggio = await response.text();
      alert(messaggio);
    } catch (err) {
      alert('Errore: ' + err.message);
    }
  };

  // Gestione Modal Modifica
  const handleAperturaModifica = (v) => {
    setVaccinazioneInModifica(v.id);
    setAnimaleIdMod(v.animaleId || v.idAnimale?.id || v.animale?.id || v.idAnimale || '');
    setTipoVaccinoIdMod(v.tipoVaccinoId || v.idTipoVaccino?.id || v.tipoVaccino?.id || v.idTipoVaccino || '');
    setDataVaccinazioneMod(v.dataVaccinazione ? v.dataVaccinazione.split('T')[0] : '');
    setLottoMod(v.lotto || '');
  };

  const handleChiudiModifica = () => {
    setVaccinazioneInModifica(null);
  };

  // Salva Modifiche
  const handleSalvaModifica = async (e) => {
    e.preventDefault();

    try {
      const payload = {
        animaleId: Number(animaleIdMod),
        tipoVaccinoId: Number(tipoVaccinoIdMod),
        dataVaccinazione: formattaLocalDateTime(dataVaccinazioneMod),
        lotto: lottoMod ? lottoMod.trim() : ''
      };

      const response = await apiFetchWithPayload(
        `/api/vaccinazioni/${vaccinazioneInModifica}`,
        payload,
        { method: 'PUT' }
      );

      if (!response.ok) {
        throw new Error(await readApiError(response, 'Errore nell\'aggiornamento'));
      }

      handleChiudiModifica();
      caricaVaccinazioni();
    } catch (err) {
      alert('Errore: ' + err.message);
    }
  };

  // Soft-delete
  const handleEliminaVaccinazione = async (id) => {
    if (!window.confirm('Sei sicuro di voler eliminare questa vaccinazione?')) return;

    try {
      // Nota: usiamo /elimina e metodo PATCH
      const response = await apiFetch(`/api/vaccinazioni/${id}/elimina`, {
        method: 'PATCH'
      });

      if (!response.ok) {
        const msg = await readApiError(response, 'Errore durante l\'eliminazione');
        throw new Error(msg);
      }

      caricaVaccinazioni();
    } catch (err) {
      alert('Errore: ' + err.message);
    }
  };

  return (
    <div>
      {/* Form Inserimento */}
      <div className="panel section-spaced-sm">
        <h3>Registra Nuova Vaccinazione</h3>
        <form className="stack-form" onSubmit={handleAggiungiVaccinazione}>

          <label>
            Animale
            <select
              className="form-control"
              value={animaleId}
              onChange={(e) => setAnimaleId(e.target.value)}
            >
              <option value="">-- Seleziona Animale --</option>
              {animali.map((a) => (
                <option key={a.id} value={a.id}>
                  {getEntityName(a)} ({a.specie || 'Animale'})
                </option>
              ))}
            </select>
          </label>

          <label>
            Tipo Vaccino
            <select
              className="form-control"
              value={tipoVaccinoId}
              onChange={(e) => setTipoVaccinoId(e.target.value)}
            >
              <option value="">-- Seleziona Tipo Vaccino --</option>
              {tipiVaccino.map((t) => (
                <option key={t.id} value={t.id}>
                  {t.tipologia || getEntityName(t)}
                </option>
              ))}
            </select>
          </label>

          <label>
            Data Somministrazione
            <input
              type="date"
              className="form-control"
              value={dataVaccinazione}
              onChange={(e) => setDataVaccinazione(e.target.value)}
            />
          </label>

          <label>
            Lotto
            <input
              type="text"
              className="form-control"
              placeholder="Es: LOT-12345"
              value={lotto}
              onChange={(e) => setLotto(e.target.value)}
            />
          </label>

          <button type="submit" className="btn btn-primary align-start">
            Salva Vaccinazione
          </button>
        </form>
      </div>

      {/* Tabella Elenco */}
      <div className="panel section-spaced-sm">
        <h3>Elenco Vaccinazioni</h3>

        {loading ? (
          <p className="muted-text">Caricamento in corso...</p>
        ) : error ? (
          <div style={{ color: '#d32f2f', padding: '10px', borderRadius: '4px', backgroundColor: '#ffebee' }}>
            Errore: {error}
          </div>
        ) : vaccinazioni.length > 0 ? (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Animale</th>
                  <th>Tipo Vaccino</th>
                  <th>Somministrazione</th>
                  <th>Lotto</th>
                  <th>Azioni</th>
                </tr>
              </thead>
              <tbody>
                {vaccinazioni.map((v) => (
                  <tr key={v.id}>
                    <td>{v.id}</td>
                    <td><strong>{renderNomeAnimale(v)}</strong></td>
                    <td>{renderTipoVaccino(v)}</td>
                    <td>{v.dataVaccinazione ? new Date(v.dataVaccinazione).toLocaleDateString() : '—'}</td>
                    <td>{v.lotto || '—'}</td>
                    <td style={{ display: 'flex', gap: '5px', flexWrap: 'wrap' }}>
                      <button
                        className="btn btn-outline btn-sm"
                        onClick={() => handleInviaPromemoria(v.id)}
                        title="Invia mail di promemoria"
                      >
                        Promemoria
                      </button>
                      <button
                        className="btn btn-outline btn-sm"
                        onClick={() => handleAperturaModifica(v)}
                      >
                        Modifica
                      </button>
                      <button
                        className="btn btn-outline btn-sm"
                        onClick={() => handleEliminaVaccinazione(v.id)}
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
          <EmptyMessage>
            Nessuna vaccinazione trovata.
          </EmptyMessage>
        )}
      </div>

      {/* Modal Modifica */}
      {vaccinazioneInModifica && (
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
            <h3>Modifica Vaccinazione #{vaccinazioneInModifica}</h3>
            <form className="stack-form" onSubmit={handleSalvaModifica}>

              <label>
                Animale
                <select
                  className="form-control"
                  value={animaleIdMod}
                  onChange={(e) => setAnimaleIdMod(e.target.value)}
                >
                  {animali.map((a) => (
                    <option key={a.id} value={a.id}>{getEntityName(a)}</option>
                  ))}
                </select>
              </label>

              <label>
                Tipo Vaccino
                <select
                  className="form-control"
                  value={tipoVaccinoIdMod}
                  onChange={(e) => setTipoVaccinoIdMod(e.target.value)}
                >
                  {tipiVaccino.map((t) => (
                    <option key={t.id} value={t.id}>{t.tipologia || getEntityName(t)}</option>
                  ))}
                </select>
              </label>

              <label>
                Data Somministrazione
                <input
                  type="date"
                  className="form-control"
                  value={dataVaccinazioneMod}
                  onChange={(e) => setDataVaccinazioneMod(e.target.value)}
                />
              </label>

              <label>
                Lotto
                <input
                  type="text"
                  className="form-control"
                  value={lottoMod}
                  onChange={(e) => setLottoMod(e.target.value)}
                />
              </label>

              <div style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
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