import { useContext, useEffect, useMemo, useState } from 'react';
import { AppContext } from '../../context/AppContext';
import {
  createAnimal,
  fetchAnimalOwners,
  fetchBreedsBySpecies,
  fetchSpecies,
} from '../../services/animalApi';
import './AddAnimalForm.css';

const initialFormData = {
  utente: '',
  nome: '',
  specie: '',
  razza: '',
  sesso: 'Maschio',
  dataNascita: '',
  peso: '',
  microchip: '',
  note: '',
};

function getEntityName(entity) {
  return entity?.nome ?? entity?.Nome ?? '';
}

function getOwnerLabel(owner) {
  return [owner.nome, owner.cognome].filter(Boolean).join(' ') || owner.email || `Utente ${owner.id}`;
}

function normalizeOptionalText(value) {
  const trimmedValue = value.trim();
  return trimmedValue || null;
}

export default function AddAnimalForm({ onCreated }) {
  const { currentUser } = useContext(AppContext);
  const [formData, setFormData] = useState(() => ({
    ...initialFormData,
    utente: currentUser?.id ? String(currentUser.id) : '',
  }));
  const [owners, setOwners] = useState([]);
  const [species, setSpecies] = useState([]);
  const [breeds, setBreeds] = useState([]);
  const [ownerSearch, setOwnerSearch] = useState('');
  const [isLoadingInitialData, setIsLoadingInitialData] = useState(true);
  const [isLoadingBreeds, setIsLoadingBreeds] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [statusMessage, setStatusMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const filteredOwners = useMemo(() => {
    const searchValue = ownerSearch.trim().toLowerCase();

    if (!searchValue) {
      return owners;
    }

    return owners.filter((owner) => {
      const searchableOwner = [
        owner.nome,
        owner.cognome,
        owner.email,
        owner.codiceFiscale,
        owner.telefono,
      ]
        .filter(Boolean)
        .join(' ')
        .toLowerCase();

      return searchableOwner.includes(searchValue);
    });
  }, [ownerSearch, owners]);

  useEffect(() => {
    let isMounted = true;

    async function loadInitialData() {
      setIsLoadingInitialData(true);
      setErrorMessage('');

      try {
        const [ownerList, speciesList] = await Promise.all([
          fetchAnimalOwners(),
          fetchSpecies(),
        ]);

        if (!isMounted) {
          return;
        }

        const activeOwners = ownerList.filter((owner) => !owner.deleted && !owner.isDeleted);
        const activeSpecies = speciesList.filter((specie) => !specie.deleted && !specie.isDeleted);

        setOwners(activeOwners);
        setSpecies(activeSpecies);
      } catch (error) {
        if (isMounted) {
          setErrorMessage(error.message);
        }
      } finally {
        if (isMounted) {
          setIsLoadingInitialData(false);
        }
      }
    }

    loadInitialData();

    return () => {
      isMounted = false;
    };
  }, []);

  useEffect(() => {
    if (!formData.specie) {
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
      ...(fieldName === 'specie' ? { razza: '' } : {}),
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setStatusMessage('');
    setErrorMessage('');
    setIsSubmitting(true);

    const selectedSpecies = species.find((specie) => String(specie.id) === formData.specie);
    const selectedBreed = breeds.find((breed) => String(breed.id) === formData.razza);

    const payload = {
      nome: formData.nome.trim(),
      specie: getEntityName(selectedSpecies),
      razza: getEntityName(selectedBreed),
      sesso: formData.sesso,
      dataNascita: formData.dataNascita,
      peso: Number(formData.peso),
      microchip: normalizeOptionalText(formData.microchip),
      note: normalizeOptionalText(formData.note),
      isDeleted: false,
      utente: {
        id: Number(formData.utente),
      },
    };

    try {
      await createAnimal(payload);
      setStatusMessage('Animale registrato correttamente.');
      setFormData({
        ...initialFormData,
        utente: currentUser?.id ? String(currentUser.id) : '',
      });
      setOwnerSearch('');
      onCreated?.();
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <form className="add-animal-form panel" onSubmit={handleSubmit}>
      <div className="add-animal-form__heading">
        <p className="eyebrow">Nuovo animale</p>
        <h2>Dati anagrafici</h2>
      </div>

      {isLoadingInitialData && (
        <p className="muted-text">Caricamento proprietari e specie...</p>
      )}

      <label htmlFor="animal-owner-search">
        Cerca proprietario
        <input
          id="animal-owner-search"
          type="search"
          value={ownerSearch}
          onChange={(event) => setOwnerSearch(event.target.value)}
          placeholder="Cerca per nome, email, codice fiscale o telefono"
          autoComplete="off"
        />
      </label>

      <label htmlFor="animal-owner">
        Proprietario
        <select
          id="animal-owner"
          name="utente"
          value={formData.utente}
          onChange={(event) => updateField('utente', event.target.value)}
          required
          disabled={isLoadingInitialData}
        >
          <option value="">Seleziona un proprietario</option>
          {filteredOwners.map((owner) => (
            <option key={owner.id} value={owner.id}>
              {getOwnerLabel(owner)}
            </option>
          ))}
        </select>
      </label>

      <div className="add-animal-form__grid">
        <label htmlFor="animal-name">
          Nome animale
          <input
            id="animal-name"
            name="nome"
            type="text"
            value={formData.nome}
            onChange={(event) => updateField('nome', event.target.value)}
            placeholder="Inserisci il nome"
            maxLength="30"
            required
          />
        </label>

        <label htmlFor="animal-birth-date">
          Data di nascita
          <input
            id="animal-birth-date"
            name="dataNascita"
            type="date"
            value={formData.dataNascita}
            onChange={(event) => updateField('dataNascita', event.target.value)}
            required
          />
        </label>
      </div>

      <div className="add-animal-form__grid">
        <label htmlFor="animal-species">
          Specie
          <select
            id="animal-species"
            name="specie"
            value={formData.specie}
            onChange={(event) => updateField('specie', event.target.value)}
            required
            disabled={isLoadingInitialData}
          >
            <option value="">Seleziona una specie</option>
            {species.map((specie) => (
              <option key={specie.id} value={specie.id}>
                {getEntityName(specie)}
              </option>
            ))}
          </select>
        </label>

        <label htmlFor="animal-breed">
          Razza
          <select
            id="animal-breed"
            name="razza"
            value={formData.razza}
            onChange={(event) => updateField('razza', event.target.value)}
            required
            disabled={!formData.specie || isLoadingBreeds}
          >
            <option value="">
              {formData.specie ? 'Seleziona una razza' : 'Seleziona prima una specie'}
            </option>
            {breeds.map((breed) => (
              <option key={breed.id} value={breed.id}>
                {getEntityName(breed)}
              </option>
            ))}
          </select>
        </label>
      </div>

      <fieldset className="add-animal-form__sex">
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

      <div className="add-animal-form__grid">
        <label htmlFor="animal-weight">
          Peso
          <input
            id="animal-weight"
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

        <label htmlFor="animal-microchip">
          Microchip
          <input
            id="animal-microchip"
            name="microchip"
            type="text"
            value={formData.microchip}
            onChange={(event) => updateField('microchip', event.target.value)}
            placeholder="Codice microchip"
            maxLength="15"
          />
        </label>
      </div>

      <label htmlFor="animal-note">
        Note
        <textarea
          id="animal-note"
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

      <button className="btn btn-primary add-animal-form__submit" type="submit" disabled={isSubmitting}>
        {isSubmitting ? 'Salvataggio...' : 'Registra animale'}
      </button>
    </form>
  );
}
