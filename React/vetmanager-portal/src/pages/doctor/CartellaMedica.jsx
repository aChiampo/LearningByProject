import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import EmptyMessage from '../../components/common/EmptyMessage';
import PageTitle from '../../components/common/PageTitle';
import { fetchAnimalById, updateAnimal } from '../../services/animalApi';
import { fetchVisitById, fetchVisitsByAnimal, updateVisitNotes } from '../../services/visitApi';
import './CartellaMedica.css';

const initialAnimalForm = {
  nome: '',
  specie: '',
  razza: '',
  sesso: '',
  dataNascita: '',
  peso: '',
  microchip: '',
  note: '',
};

function formatDateTime(value) {
  if (!value) {
    return 'N/D';
  }

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return new Intl.DateTimeFormat('it-IT', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(date);
}

function toDateValue(value) {
  return value ? String(value).slice(0, 10) : '';
}

function getOwnerName(animal) {
  const owner = animal?.proprietario ?? animal?.raw?.utente ?? {};
  return [owner.nome, owner.cognome].filter(Boolean).join(' ') || owner.email || 'N/D';
}

function normalizeOptionalText(value) {
  const trimmedValue = value.trim();
  return trimmedValue || null;
}

function buildAnimalForm(animal) {
  return {
    nome: animal?.nome ?? '',
    specie: animal?.specie ?? '',
    razza: animal?.razza ?? '',
    sesso: animal?.sesso ?? '',
    dataNascita: toDateValue(animal?.dataNascita),
    peso: animal?.peso != null ? String(animal.peso) : '',
    microchip: animal?.microchip ?? '',
    note: animal?.note ?? '',
  };
}

function sortVisitsByMostRecent(visits) {
  return [...visits].sort((firstVisit, secondVisit) => {
    const firstDate = new Date(firstVisit.dataVisita).getTime();
    const secondDate = new Date(secondVisit.dataVisita).getTime();

    return (Number.isNaN(secondDate) ? 0 : secondDate) - (Number.isNaN(firstDate) ? 0 : firstDate);
  });
}

export default function CartellaMedica() {
  const { animalId } = useParams();
  const navigate = useNavigate();
  const [animal, setAnimal] = useState(null);
  const [animalForm, setAnimalForm] = useState(initialAnimalForm);
  const [visits, setVisits] = useState([]);
  const [selectedVisit, setSelectedVisit] = useState(null);
  const [reportVisit, setReportVisit] = useState(null);
  const [reportForm, setReportForm] = useState({ note: '' });
  const [isLoading, setIsLoading] = useState(true);
  const [isSavingAnimal, setIsSavingAnimal] = useState(false);
  const [isLoadingVisitDetail, setIsLoadingVisitDetail] = useState(false);
  const [isSavingReport, setIsSavingReport] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [statusMessage, setStatusMessage] = useState('');

  const orderedVisits = useMemo(() => sortVisitsByMostRecent(visits), [visits]);

  useEffect(() => {
    let isMounted = true;

    async function loadMedicalRecord() {
      setIsLoading(true);
      setErrorMessage('');
      setStatusMessage('');

      try {
        const [animalData, visitList] = await Promise.all([
          fetchAnimalById(animalId),
          fetchVisitsByAnimal(animalId),
        ]);

        if (!isMounted) {
          return;
        }

        setAnimal(animalData);
        setAnimalForm(buildAnimalForm(animalData));
        setVisits(visitList);
      } catch (error) {
        if (isMounted) {
          setErrorMessage(error.message);
          setAnimal(null);
          setVisits([]);
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    loadMedicalRecord();

    return () => {
      isMounted = false;
    };
  }, [animalId]);

  function updateAnimalField(fieldName, value) {
    setAnimalForm((currentForm) => ({
      ...currentForm,
      [fieldName]: value,
    }));
  }

  async function handleSaveAnimal(event) {
    event.preventDefault();
    setIsSavingAnimal(true);
    setErrorMessage('');
    setStatusMessage('');

    const payload = {
      ...animal?.raw,
      nome: animalForm.nome.trim(),
      specie: animalForm.specie.trim(),
      razza: animalForm.razza.trim(),
      sesso: animalForm.sesso,
      dataNascita: animalForm.dataNascita,
      peso: animalForm.peso === '' ? null : Number(animalForm.peso),
      microchip: normalizeOptionalText(animalForm.microchip),
      note: normalizeOptionalText(animalForm.note),
      isDeleted: animal?.raw?.isDeleted ?? false,
      utente: animal?.raw?.utente ?? animal?.proprietario,
    };

    try {
      const updatedAnimal = await updateAnimal(animalId, payload);
      setAnimal(updatedAnimal);
      setAnimalForm(buildAnimalForm(updatedAnimal));
      setStatusMessage('Dati anagrafici salvati correttamente.');
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setIsSavingAnimal(false);
    }
  }

  async function handleShowVisitDetail(visit) {
    setIsLoadingVisitDetail(true);
    setErrorMessage('');
    setStatusMessage('');
    setReportVisit(null);

    try {
      const visitDetail = await fetchVisitById(visit.id);
      setSelectedVisit(visitDetail);
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setIsLoadingVisitDetail(false);
    }
  }

  function handleOpenReportForm(visit) {
    setSelectedVisit(null);
    setReportVisit(visit);
    setReportForm({
      note: visit.note ?? '',
    });
    setStatusMessage('');
    setErrorMessage('');
  }

  async function handleSaveReport(event) {
    event.preventDefault();
    setIsSavingReport(true);
    setErrorMessage('');
    setStatusMessage('');

    try {
      const updatedVisit = await updateVisitNotes(reportVisit.id, reportForm.note);
      setReportVisit(updatedVisit);
      setVisits((currentVisits) => currentVisits.map((visit) => (
        visit.id === updatedVisit.id ? updatedVisit : visit
      )));
      setStatusMessage('Report visita salvato correttamente.');
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setIsSavingReport(false);
    }
  }

  return (
    <div className="cartella-medica">
      <PageTitle eyebrow="Archivio clinico" title="Cartella Medica" />

      <button
        className="btn btn-outline btn-sm cartella-medica__back"
        type="button"
        onClick={() => navigate('/doctor/animals')}
      >
        Torna al registro
      </button>

      {isLoading && <p className="muted-text">Caricamento cartella medica...</p>}

      {errorMessage && (
        <p className="form-status form-status--error">{errorMessage}</p>
      )}

      {statusMessage && (
        <p className="form-status form-status--success">{statusMessage}</p>
      )}

      {!isLoading && animal && (
        <>
          <form className="panel cartella-medica__section" onSubmit={handleSaveAnimal}>
            <div className="cartella-medica__heading">
              <div>
                <p className="eyebrow">Paziente</p>
                <h2>Dati anagrafici</h2>
              </div>
              <div className="cartella-medica__owner">
                <span>Proprietario</span>
                <strong>{getOwnerName(animal)}</strong>
              </div>
            </div>

            <div className="cartella-medica__form-grid">
              <label htmlFor="medical-record-name">
                Nome
                <input
                  id="medical-record-name"
                  name="nome"
                  type="text"
                  value={animalForm.nome}
                  onChange={(event) => updateAnimalField('nome', event.target.value)}
                  maxLength="30"
                  required
                />
              </label>

              <label htmlFor="medical-record-birth-date">
                Data di nascita
                <input
                  id="medical-record-birth-date"
                  name="dataNascita"
                  type="date"
                  value={animalForm.dataNascita}
                  onChange={(event) => updateAnimalField('dataNascita', event.target.value)}
                  required
                />
              </label>
            </div>

            <div className="cartella-medica__form-grid">
              <label htmlFor="medical-record-species">
                Specie
                <input
                  id="medical-record-species"
                  name="specie"
                  type="text"
                  value={animalForm.specie}
                  onChange={(event) => updateAnimalField('specie', event.target.value)}
                />
              </label>

              <label htmlFor="medical-record-breed">
                Razza
                <input
                  id="medical-record-breed"
                  name="razza"
                  type="text"
                  value={animalForm.razza}
                  onChange={(event) => updateAnimalField('razza', event.target.value)}
                />
              </label>
            </div>

            <fieldset className="cartella-medica__sex">
              <legend>Sesso</legend>
              <label>
                <input
                  type="radio"
                  name="sesso"
                  value="Maschio"
                  checked={animalForm.sesso === 'Maschio'}
                  onChange={(event) => updateAnimalField('sesso', event.target.value)}
                />
                <span>Maschio</span>
              </label>
              <label>
                <input
                  type="radio"
                  name="sesso"
                  value="Femmina"
                  checked={animalForm.sesso === 'Femmina'}
                  onChange={(event) => updateAnimalField('sesso', event.target.value)}
                />
                <span>Femmina</span>
              </label>
            </fieldset>

            <div className="cartella-medica__form-grid">
              <label htmlFor="medical-record-weight">
                Peso
                <input
                  id="medical-record-weight"
                  name="peso"
                  type="number"
                  min="0"
                  step="0.01"
                  value={animalForm.peso}
                  onChange={(event) => updateAnimalField('peso', event.target.value)}
                />
              </label>

              <label htmlFor="medical-record-microchip">
                Microchip
                <input
                  id="medical-record-microchip"
                  name="microchip"
                  type="text"
                  value={animalForm.microchip}
                  onChange={(event) => updateAnimalField('microchip', event.target.value)}
                  maxLength="15"
                />
              </label>
            </div>

            <label htmlFor="medical-record-notes">
              Note
              <textarea
                id="medical-record-notes"
                name="note"
                value={animalForm.note}
                onChange={(event) => updateAnimalField('note', event.target.value)}
                rows="4"
              />
            </label>

            <button className="btn btn-primary cartella-medica__save" type="submit" disabled={isSavingAnimal}>
              {isSavingAnimal ? 'Salvataggio...' : 'Salva'}
            </button>
          </form>

          <section className="panel cartella-medica__section">
            <div className="cartella-medica__heading">
              <div>
                <p className="eyebrow">Storico clinico</p>
                <h2>Visite precedenti</h2>
              </div>
              <span className="badge">{orderedVisits.length} visite</span>
            </div>

            {orderedVisits.length > 0 ? (
              <div className="cartella-medica__visit-list">
                {orderedVisits.map((visit) => (
                  <article
                    className={`cartella-medica__visit-card cartella-medica__visit-card--${visit.stato.toLowerCase()}`}
                    key={visit.id}
                  >
                    <div className="cartella-medica__visit-main">
                      <span className="cartella-medica__status">{visit.stato || 'N/D'}</span>
                      <h3>{visit.tipoVisita || 'Tipo visita non disponibile'}</h3>
                      <p>{formatDateTime(visit.dataVisita)}</p>
                    </div>

                    <p className="cartella-medica__visit-note">
                      {visit.note || 'Nessuna nota registrata.'}
                    </p>

                    <div className="cartella-medica__visit-actions">
                      {visit.stato === 'COMPLETATA' ? (
                        <button
                          className="btn btn-outline btn-sm"
                          type="button"
                          onClick={() => handleShowVisitDetail(visit)}
                          disabled={isLoadingVisitDetail}
                        >
                          Vedi dettaglio
                        </button>
                      ) : (
                        <button
                          className="btn btn-secondary btn-sm"
                          type="button"
                          onClick={() => handleOpenReportForm(visit)}
                        >
                          Completa Report
                        </button>
                      )}
                    </div>
                  </article>
                ))}
              </div>
            ) : (
              <EmptyMessage>Nessuna visita registrata per questo animale.</EmptyMessage>
            )}
          </section>

          {selectedVisit && (
            <section className="panel cartella-medica__section cartella-medica__detail">
              <div className="cartella-medica__heading">
                <div>
                  <p className="eyebrow">Dettaglio visita</p>
                  <h2>{selectedVisit.tipoVisita || 'Visita completata'}</h2>
                </div>
                <span className="badge badge-success">{selectedVisit.stato}</span>
              </div>

              <dl className="cartella-medica__details">
                <div>
                  <dt>Data visita</dt>
                  <dd>{formatDateTime(selectedVisit.dataVisita)}</dd>
                </div>
                <div>
                  <dt>Veterinario</dt>
                  <dd>{selectedVisit.veterinarioNome || 'N/D'}</dd>
                </div>
                <div>
                  <dt>Pagamento</dt>
                  <dd>{selectedVisit.pagamento ? 'Associato' : 'Non associato'}</dd>
                </div>
                <div>
                  <dt>Nota privata</dt>
                  <dd>{selectedVisit.notaPrivata || 'N/D'}</dd>
                </div>
              </dl>

              <div className="cartella-medica__note-panel">
                <h3>Note visita</h3>
                <p>{selectedVisit.note || 'Nessuna nota registrata.'}</p>
              </div>
            </section>
          )}

          {reportVisit && (
            <form className="panel cartella-medica__section cartella-medica__report" onSubmit={handleSaveReport}>
              <div className="cartella-medica__heading">
                <div>
                  <p className="eyebrow">Report visita</p>
                  <h2>Completa Report</h2>
                </div>
                <span className="badge badge-neutral">{reportVisit.stato}</span>
              </div>

              <div className="cartella-medica__form-grid">
                <label htmlFor="report-visit-type">
                  Tipo visita
                  <input id="report-visit-type" type="text" value={reportVisit.tipoVisita || 'N/D'} readOnly />
                </label>

                <label htmlFor="report-visit-date">
                  Data visita
                  <input id="report-visit-date" type="text" value={formatDateTime(reportVisit.dataVisita)} readOnly />
                </label>
              </div>

              <label htmlFor="report-visit-notes">
                Note visita
                <textarea
                  id="report-visit-notes"
                  name="note"
                  value={reportForm.note}
                  onChange={(event) => setReportForm({ note: event.target.value })}
                  rows="5"
                />
              </label>

              <div className="cartella-medica__actions">
                <button className="btn btn-primary" type="submit" disabled={isSavingReport}>
                  {isSavingReport ? 'Salvataggio...' : 'Salva Report'}
                </button>
                <button
                  className="btn btn-outline"
                  type="button"
                  onClick={() => setReportVisit(null)}
                >
                  Annulla
                </button>
              </div>
            </form>
          )}
        </>
      )}
    </div>
  );
}
