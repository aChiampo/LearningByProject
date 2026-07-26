import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import EmptyMessage from '../../components/common/EmptyMessage';
import PageTitle from '../../components/common/PageTitle';
import AnimalVaccinationCard from '../../components/vaccinations/AnimalVaccinationCard';
import { fetchAnimalById, updateAnimal } from '../../services/animalApi';
import { fetchVisitById, fetchVisitsByAnimal, updateVisitNotes } from '../../services/visitApi';
import {
  createVaccination,
  fetchVaccinationsByAnimal,
  fetchVaccineTypes,
  getTodayDateValue,
  renewVaccination,
} from '../../services/vaccinationApi';
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

const initialVaccinationForm = {
  tipoVaccinoId: '',
  dataVaccinazione: getTodayDateValue(),
  lotto: '',
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

function sortVaccinationsByExpiration(vaccinations) {
  return [...vaccinations].sort((firstVaccination, secondVaccination) => {
    const firstDate = new Date(firstVaccination.dataScadenza).getTime();
    const secondDate = new Date(secondVaccination.dataScadenza).getTime();

    return (Number.isNaN(firstDate) ? Number.MAX_SAFE_INTEGER : firstDate)
      - (Number.isNaN(secondDate) ? Number.MAX_SAFE_INTEGER : secondDate);
  });
}

export default function CartellaMedica({ readOnly = false }) {
  const { animalId } = useParams();
  const navigate = useNavigate();
  const [animal, setAnimal] = useState(null);
  const [animalForm, setAnimalForm] = useState(initialAnimalForm);
  const [visits, setVisits] = useState([]);
  const [vaccinations, setVaccinations] = useState([]);
  const [vaccineTypes, setVaccineTypes] = useState([]);
  const [vaccinationForm, setVaccinationForm] = useState(initialVaccinationForm);
  const [isVaccinationFormOpen, setIsVaccinationFormOpen] = useState(false);
  const [isSavingVaccination, setIsSavingVaccination] = useState(false);
  const [renewingVaccinationId, setRenewingVaccinationId] = useState(null);
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
  const orderedVaccinations = useMemo(() => sortVaccinationsByExpiration(vaccinations), [vaccinations]);

  useEffect(() => {
    let isMounted = true;

    async function loadMedicalRecord() {
      setIsLoading(true);
      setErrorMessage('');
      setStatusMessage('');

      try {
        const [animalData, visitList, vaccinationList, vaccineTypeList] = await Promise.all([
          fetchAnimalById(animalId),
          fetchVisitsByAnimal(animalId),
          fetchVaccinationsByAnimal(animalId),
          fetchVaccineTypes(),
        ]);

        if (!isMounted) {
          return;
        }

        setAnimal(animalData);
        setAnimalForm(buildAnimalForm(animalData));
        setVisits(visitList);
        setVaccinations(vaccinationList);
        setVaccineTypes(vaccineTypeList);
      } catch (error) {
        if (isMounted) {
          setErrorMessage(error.message);
          setAnimal(null);
          setVisits([]);
          setVaccinations([]);
          setVaccineTypes([]);
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

  function updateVaccinationField(fieldName, value) {
    setVaccinationForm((currentForm) => ({
      ...currentForm,
      [fieldName]: value,
    }));
  }

  function handleOpenVaccinationForm() {
    setVaccinationForm({
      ...initialVaccinationForm,
      tipoVaccinoId: vaccineTypes[0]?.id ? String(vaccineTypes[0].id) : '',
      dataVaccinazione: getTodayDateValue(),
    });
    setIsVaccinationFormOpen(true);
    setStatusMessage('');
    setErrorMessage('');
  }

  async function handleSaveVaccination(event) {
    event.preventDefault();

    if (readOnly) {
      return;
    }

    setIsSavingVaccination(true);
    setErrorMessage('');
    setStatusMessage('');

    try {
      const createdVaccination = await createVaccination({
        animaleId: animalId,
        ...vaccinationForm,
      });
      setVaccinations((currentVaccinations) => [...currentVaccinations, createdVaccination]);
      setVaccinationForm({
        ...initialVaccinationForm,
        dataVaccinazione: getTodayDateValue(),
      });
      setIsVaccinationFormOpen(false);
      setStatusMessage('Vaccinazione aggiunta correttamente.');
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setIsSavingVaccination(false);
    }
  }

  async function handleRenewVaccination(vaccination) {
    if (readOnly) {
      return;
    }

    setRenewingVaccinationId(vaccination.id);
    setErrorMessage('');
    setStatusMessage('');

    try {
      const renewedVaccination = await renewVaccination(vaccination, getTodayDateValue());
      setVaccinations((currentVaccinations) => currentVaccinations.map((currentVaccination) => (
        currentVaccination.id === renewedVaccination.id ? renewedVaccination : currentVaccination
      )));
      setStatusMessage('Periodo vaccinazione rinnovato correttamente.');
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setRenewingVaccinationId(null);
    }
  }

  async function handleSaveAnimal(event) {
    event.preventDefault();

    if (readOnly) {
      return;
    }

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
    if (readOnly) {
      return;
    }

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

    if (readOnly) {
      return;
    }

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
        onClick={() => navigate(readOnly ? '/client/dashboard' : '/doctor/animals')}
      >
        {readOnly ? 'Torna ai miei animali' : 'Torna al registro'}
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
                  readOnly={readOnly}
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
                  readOnly={readOnly}
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
                  readOnly={readOnly}
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
                  readOnly={readOnly}
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
                  disabled={readOnly}
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
                  disabled={readOnly}
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
                  readOnly={readOnly}
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
                  readOnly={readOnly}
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
                readOnly={readOnly}
              />
            </label>

            {!readOnly && (
              <button className="btn btn-primary cartella-medica__save" type="submit" disabled={isSavingAnimal}>
                {isSavingAnimal ? 'Salvataggio...' : 'Salva'}
              </button>
            )}
          </form>

          <section className="panel cartella-medica__section">
            <div className="cartella-medica__heading">
              <div>
                <p className="eyebrow">Profilassi</p>
                <h2>Vaccinazioni</h2>
              </div>
              <div className="cartella-medica__heading-actions">
                <span className="badge">{orderedVaccinations.length} vaccinazioni</span>
                {!readOnly && (
                  <button
                    className="btn btn-primary btn-sm"
                    type="button"
                    onClick={handleOpenVaccinationForm}
                    disabled={vaccineTypes.length === 0}
                  >
                    Aggiungi vaccinazione
                  </button>
                )}
              </div>
            </div>

            {!readOnly && vaccineTypes.length === 0 && (
              <p className="muted-text">Configura almeno un tipo di vaccino prima di registrare una vaccinazione.</p>
            )}

            {isVaccinationFormOpen && (
              <form className="cartella-medica__vaccination-form" onSubmit={handleSaveVaccination}>
                <div className="cartella-medica__form-grid">
                  <label htmlFor="medical-record-vaccine-type">
                    Vaccino
                    <select
                      id="medical-record-vaccine-type"
                      name="tipoVaccinoId"
                      value={vaccinationForm.tipoVaccinoId}
                      onChange={(event) => updateVaccinationField('tipoVaccinoId', event.target.value)}
                      required
                    >
                      <option value="">Seleziona vaccino</option>
                      {vaccineTypes.map((vaccineType) => (
                        <option key={vaccineType.id} value={vaccineType.id}>
                          {vaccineType.nome} - {vaccineType.durataMesi} mesi
                        </option>
                      ))}
                    </select>
                  </label>

                  <label htmlFor="medical-record-vaccination-date">
                    Data vaccinazione
                    <input
                      id="medical-record-vaccination-date"
                      name="dataVaccinazione"
                      type="date"
                      value={vaccinationForm.dataVaccinazione}
                      onChange={(event) => updateVaccinationField('dataVaccinazione', event.target.value)}
                      required
                    />
                  </label>
                </div>

                <label htmlFor="medical-record-vaccination-lot">
                  Lotto
                  <input
                    id="medical-record-vaccination-lot"
                    name="lotto"
                    type="text"
                    value={vaccinationForm.lotto}
                    onChange={(event) => updateVaccinationField('lotto', event.target.value)}
                    maxLength="80"
                  />
                </label>

                <div className="cartella-medica__actions">
                  <button className="btn btn-primary" type="submit" disabled={isSavingVaccination}>
                    {isSavingVaccination ? 'Salvataggio...' : 'Salva vaccinazione'}
                  </button>
                  <button
                    className="btn btn-outline"
                    type="button"
                    onClick={() => setIsVaccinationFormOpen(false)}
                  >
                    Annulla
                  </button>
                </div>
              </form>
            )}

            {orderedVaccinations.length > 0 ? (
              <div className="cartella-medica__vaccination-list">
                {orderedVaccinations.map((vaccination) => (
                  <AnimalVaccinationCard
                    key={vaccination.id}
                    vaccination={vaccination}
                    readOnly={readOnly}
                    isRenewing={renewingVaccinationId === vaccination.id}
                    onRenew={handleRenewVaccination}
                  />
                ))}
              </div>
            ) : (
              <EmptyMessage>Nessuna vaccinazione registrata per questo animale.</EmptyMessage>
            )}
          </section>

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
                      {readOnly || visit.stato === 'COMPLETATA' ? (
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
                {!readOnly && (
                  <div>
                    <dt>Nota privata</dt>
                    <dd>{selectedVisit.notaPrivata || 'N/D'}</dd>
                  </div>
                )}
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
