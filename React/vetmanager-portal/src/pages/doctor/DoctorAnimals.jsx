import { useEffect, useMemo, useState } from 'react';
import PageTitle from '../../components/common/PageTitle';
import EmptyMessage from '../../components/common/EmptyMessage';
import { fetchAllAnimals } from '../../services/animalApi';

function getOwnerName(animal) {
  const owner = animal.proprietario ?? {};
  return [owner.nome, owner.cognome].filter(Boolean).join(' ') || owner.email || 'N/D';
}

export default function DoctorAnimals() {
  const [animals, setAnimals] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [loadError, setLoadError] = useState('');

  const filteredAnimals = useMemo(() => {
    const normalizedSearchTerm = searchTerm.trim().toLowerCase();

    if (!normalizedSearchTerm) {
      return animals;
    }

    return animals.filter((animal) => {
      const searchableText = [
        animal.nome,
        animal.specie,
        animal.razza,
        getOwnerName(animal),
      ].join(' ').toLowerCase();

      return searchableText.includes(normalizedSearchTerm);
    });
  }, [animals, searchTerm]);

  useEffect(() => {
    let isMounted = true;

    async function loadAnimals() {
      setIsLoading(true);
      setLoadError('');

      try {
        const animalList = await fetchAllAnimals();

        if (isMounted) {
          setAnimals(animalList);
        }
      } catch (error) {
        if (isMounted) {
          setLoadError(error.message);
          setAnimals([]);
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    loadAnimals();

    return () => {
      isMounted = false;
    };
  }, []);

  return (
    <div>
      <PageTitle eyebrow="Archivio Clinico" title="Registro Animali" />

      <div className="panel section-spaced-sm">
        <div className="search-row">
          <input
            type="text"
            placeholder="Cerca animale per nome o proprietario..."
            value={searchTerm}
            onChange={(event) => setSearchTerm(event.target.value)}
          />
          <button className="btn btn-primary" onClick={() => setSearchTerm('')}>
            Reset
          </button>
        </div>

        {isLoading && <p className="muted-text">Caricamento registro animali...</p>}

        {loadError && (
          <p className="form-status form-status--error">
            {loadError}
          </p>
        )}

        {!isLoading && filteredAnimals.length > 0 ? (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Paziente</th>
                  <th>Specie / Razza</th>
                  <th>Ultima Visita</th>
                  <th>Proprietario</th>
                  <th>Azioni</th>
                </tr>
              </thead>
              <tbody>
                {filteredAnimals.map((patient, index) => (
                  <tr key={index}>
                    <td><strong>{patient.nome}</strong></td>
                    <td>{patient.specie} / {patient.razza}</td>
                    <td>{patient.ultimaVisita ?? 'N/D'}</td>
                    <td>{getOwnerName(patient)}</td>
                    <td>
                      <button className="btn btn-outline btn-sm" onClick={() => window.alert(`Mostra Storico Referti di ${patient.nome}`)}>
                        Vedi Cartella
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : !isLoading && (
          <EmptyMessage>Nessun animale presente nel registro clinico globale.</EmptyMessage>
        )}
      </div>
    </div>
  );
}
