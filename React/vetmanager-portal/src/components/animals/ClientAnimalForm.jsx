import { useContext, useEffect, useMemo, useState } from 'react';
import { AppContext } from '../../context/AppContext';
import {
  createAnimal,
  fetchBreedsBySpecies,
  fetchSpecies,
  sendAnimalEvaluationRequest,
} from '../../services/animalApi';
import './ClientAnimalForm.css';

const ALTRO_VALUE = 'ALTRO';
const CUSTOM_ANIMAL_DESCRIPTION =
  'La razza del tuo animale non è ancora disponibile nel nostro sistema, aggiungi una descrizione accurata del tuo animale, specificandone la specie. I nostri veterinari verificheranno se è possibile accoglierlo e riceverai una notifica via mail.';

const initialFormData = {
  nome: '',
  specie: '',
  razza: '',
  sesso: 'Maschio',
  dataNascita: '',
  peso: '',
  microchip: '',
  note: '',
  descrizione: '',
};

function getEntityName(entity) {
  return entity?.nome ?? entity?.Nome ?? '';
}

function normalizeOptionalText(value) {
  const trimmedValue = value.trim();
  return trimmedValue || null;
}

function getClientLabel(currentUser) {
  return [currentUser?.nome, currentUser?.cognome].filter(Boolean).join(' ') || currentUser?.email || 'Utente corrente';
}

export default function ClientAnimalForm({ onCreated }) {
  const { currentUser } = useContext(AppContext);
  const [formData, setFormData] = useState(initialFormData);
  const [species, setSpecies] = useState([]);
  const [breeds, setBreeds] = useState([]);
  const [isLoadingSpecies, setIsLoadingSpecies] = useState(true);
  const [isLoadingBreeds, setIsLoadingBreeds] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [statusMessage, setStatusMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const selectedSpecies = useMemo(
    () => species.find((specie) => String(specie.id) === formData.specie),
    [formData.specie, species],
  );

  const selectedBreed = useMemo(
    () => breeds.find((breed) => String(breed.id) === formData.razza),
    [breeds, formData.razza],
  );

  const isCustomAnimalRequest = formData.specie === ALTRO_VALUE || formData.razza === ALTRO_VALUE;

  useEffect(() => {
    let isMounted = true;

    async function loadSpecies() {
      setIsLoadingSpecies(true);
      setErrorMessage('');

      try {
        const speciesList = await fetchSpecies();

        if (!isMounted) {
          return;
        }

        setSpecies(speciesList.filter((specie) => !specie.deleted && !specie.isDeleted));
      } catch (error) {
        if (isMounted) {
          setErrorMessage(error.message);
        }
      } finally {
        if (isMounted) {
          setIsLoadingSpecies(false);
        }
      }
    }

    loadSpecies();

    return () => {
      isMounted = false;
    };
  }, []);

  useEffect(() => {
    if (!formData.specie || formData.specie === ALTRO_VALUE) {
      return;
    }

    let isMounted = true;

    async function loadBreeds() {
      setIsLoadingBreeds(true);
      setErrorMessage('');

      try {
        const breedList = await fetchBreedsBySpecies(formData.specie);

        if (!isMounted) {
          return;
        }

        setBreeds(breedList.filter((breed) => !breed.deleted && !breed.isDeleted));
      } catch (error) {
        if (isMounted) {
          setBreeds([]);
          setErrorMessage(error.message);
        }
      } finally {
        if (isMounted) {
          setIsLoadingBreeds(false);
        }
      }
    }

    loadBreeds();

    return () => {
      isMounted = false;
    };
  }, [formData.specie]);

  function updateField(fieldName, value) {
    if (fieldName === 'specie') {
      setBreeds([]);
    }

    setFormData((currentData) => ({
      ...currentData,
      [fieldName]: value,
      ...(fieldName === 'specie'
        ? { razza: value === ALTRO_VALUE ? ALTRO_VALUE : '' }
        : {}),
    }));
  }

  function resetForm() {
    setFormData(initialFormData);
    setBreeds([]);
  }

  function buildCommonPayload() {
    return {
      nome: formData.nome.trim(),
      specie: formData.specie === ALTRO_VALUE ? 'Altro' : getEntityName(selectedSpecies),
      razza: formData.razza === ALTRO_VALUE ? 'Altro' : getEntityName(selectedBreed),
      sesso: formData.sesso,
      dataNascita: formData.dataNascita,
      peso: Number(formData.peso),
      microchip: normalizeOptionalText(formData.microchip),
      note: normalizeOptionalText(formData.note),
    };
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setStatusMessage('');
    setErrorMessage('');

    if (!currentUser?.id) {
      setErrorMessage('Impossibile identificare il cliente collegato.');
      return;
    }

    setIsSubmitting(true);

    try {
      if (isCustomAnimalRequest) {
        const response = await sendAnimalEvaluationRequest({
          ...buildCommonPayload(),
          descrizione: formData.descrizione.trim(),
        });

        setStatusMessage(response.messaggio ?? 'Richiesta inviata correttamente. Riceverai una risposta via mail.');
        resetForm();
        return;
      }

      await createAnimal({
        ...buildCommonPayload(),
        isDeleted: false,
        utente: {
          id: currentUser.id,
        },
      });

      setStatusMessage('Animale registrato correttamente.');
      resetForm();
      onCreated?.();
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <form className="client-animal-form panel" onSubmit={handleSubmit}>
      <div className="client-animal-form__heading">
        <p className="eyebrow">Nuovo animale</p>
        <h2>Dati anagrafici</h2>
      </div>

      <div className="client-animal-form__owner">
        <span>Proprietario</span>
        <strong>{getClientLabel(currentUser)}</strong>
        {currentUser?.email && <small>{currentUser.email}</small>}
      </div>

      {isLoadingSpecies && (
        <p className="muted-text">Caricamento specie...</p>
      )}

      <div className="client-animal-form__grid">
        <label htmlFor="client-animal-name">
          Nome animale
          <input
            id="client-animal-name"
            name="nome"
            type="text"
            value={formData.nome}
            onChange={(event) => updateField('nome', event.target.value)}
            placeholder="Inserisci il nome"
            maxLength="30"
            required
          />
        </label>

        <label htmlFor="client-animal-birth-date">
          Data di nascita
          <input
            id="client-animal-birth-date"
            name="dataNascita"
            type="date"
            value={formData.dataNascita}
            onChange={(event) => updateField('dataNascita', event.target.value)}
            required
          />
        </label>
      </div>

      <div className="client-animal-form__grid">
        <label htmlFor="client-animal-species">
          Specie
          <select
            id="client-animal-species"
            name="specie"
            value={formData.specie}
            onChange={(event) => updateField('specie', event.target.value)}
            required
            disabled={isLoadingSpecies}
          >
            <option value="">Seleziona una specie</option>
            {species.map((specie) => (
              <option key={specie.id} value={specie.id}>
                {getEntityName(specie)}
              </option>
            ))}
            <option value={ALTRO_VALUE}>Altro</option>
          </select>
        </label>

        <label htmlFor="client-animal-breed">
          Razza
          <select
            id="client-animal-breed"
            name="razza"
            value={formData.razza}
            onChange={(event) => updateField('razza', event.target.value)}
            required
            disabled={!formData.specie || formData.specie === ALTRO_VALUE || isLoadingBreeds}
          >
            <option value="">
              {formData.specie ? 'Seleziona una razza' : 'Seleziona prima una specie'}
            </option>
            {formData.specie === ALTRO_VALUE ? (
              <option value={ALTRO_VALUE}>Altro</option>
            ) : (
              <>
                {breeds.map((breed) => (
                  <option key={breed.id} value={breed.id}>
                    {getEntityName(breed)}
                  </option>
                ))}
                <option value={ALTRO_VALUE}>Altro</option>
              </>
            )}
          </select>
        </label>
      </div>

      {isCustomAnimalRequest && (
        <label htmlFor="client-animal-description" className="client-animal-form__description">
          {CUSTOM_ANIMAL_DESCRIPTION}
          <textarea
            id="client-animal-description"
            name="descrizione"
            value={formData.descrizione}
            onChange={(event) => updateField('descrizione', event.target.value)}
            placeholder="Descrivi specie, razza, caratteristiche fisiche, comportamento e informazioni utili"
            rows="5"
            required
          />
        </label>
      )}

      <fieldset className="client-animal-form__sex">
        <legend>Sesso</legend>
        <label>
          <input
            type="radio"
            name="sesso"
            value="Maschio"
            checked={formData.sesso === 'Maschio'}
            onChange={(event) => updateField('sesso', event.target.value)}
          />
          <span>Maschio</span>
        </label>
        <label>
          <input
            type="radio"
            name="sesso"
            value="Femmina"
            checked={formData.sesso === 'Femmina'}
            onChange={(event) => updateField('sesso', event.target.value)}
          />
          <span>Femmina</span>
        </label>
      </fieldset>

      <div className="client-animal-form__grid">
        <label htmlFor="client-animal-weight">
          Peso
          <input
            id="client-animal-weight"
            name="peso"
            type="number"
            min="0"
            step="0.01"
            value={formData.peso}
            onChange={(event) => updateField('peso', event.target.value)}
            placeholder="Es. 12.50"
            required
          />
        </label>

        <label htmlFor="client-animal-microchip">
          Microchip
          <input
            id="client-animal-microchip"
            name="microchip"
            type="text"
            value={formData.microchip}
            onChange={(event) => updateField('microchip', event.target.value)}
            placeholder="Codice microchip"
            maxLength="15"
          />
        </label>
      </div>

      <label htmlFor="client-animal-note">
        Note
        <textarea
          id="client-animal-note"
          name="note"
          value={formData.note}
          onChange={(event) => updateField('note', event.target.value)}
          placeholder="Aggiungi eventuali informazioni utili"
          rows="4"
        />
      </label>

      {errorMessage && (
        <p className="form-status form-status--error">{errorMessage}</p>
      )}

      {statusMessage && (
        <p className="form-status form-status--success">{statusMessage}</p>
      )}

      <button className="btn btn-primary client-animal-form__submit" type="submit" disabled={isSubmitting}>
        {isSubmitting
          ? 'Invio in corso...'
          : isCustomAnimalRequest
            ? 'Invia richiesta al veterinario'
            : 'Registra animale'}
      </button>
    </form>
  );
}
