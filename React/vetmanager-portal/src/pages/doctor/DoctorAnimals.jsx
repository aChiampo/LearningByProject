import { useContext, useState } from 'react';
import { AppContext } from '../../context/AppContext';
import { ROLE_CONFIG } from '../../data/roleConfig';
import PageTitle from '../../components/common/PageTitle';
import EmptyMessage from '../../components/common/EmptyMessage';
import AddAnimalForm from '../../components/animals/AddAnimalForm';

export default function DoctorAnimals() {
  const { currentRole } = useContext(AppContext);
  const config = ROLE_CONFIG[currentRole];
  const [isAddAnimalFormOpen, setIsAddAnimalFormOpen] = useState(false);

  return (
    <div>
      <PageTitle eyebrow="Archivio Clinico" title="Registro Animali" />

      <div className="panel section-spaced-sm">
        <div className="search-row">
          <input type="text" placeholder="Cerca animale per nome o proprietario..." />
          <button className="btn btn-primary" onClick={() => window.alert('Simulazione ricerca effettiva')}>
            Cerca
          </button>
          <button
            className="btn btn-secondary"
            type="button"
            onClick={() => setIsAddAnimalFormOpen((isOpen) => !isOpen)}
          >
            {isAddAnimalFormOpen ? 'Chiudi form' : 'Aggiungi animale'}
          </button>
        </div>

        {isAddAnimalFormOpen && (
          <div className="section-spaced-sm">
            <AddAnimalForm onCreated={() => setIsAddAnimalFormOpen(false)} />
          </div>
        )}

        {config?.patientsRegistry?.length > 0 ? (
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
                {config.patientsRegistry.map((patient, index) => (
                  <tr key={index}>
                    <td><strong>{patient.nome}</strong></td>
                    <td>{patient.specie} / {patient.razza}</td>
                    <td>{patient.ultimaVisita}</td>
                    <td>{patient.proprietario}</td>
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
        ) : (
          <EmptyMessage>Nessun animale presente nel registro clinico globale.</EmptyMessage>
        )}
      </div>
    </div>
  );
}
