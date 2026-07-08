import { useContext } from 'react';
import { AppContext } from '../../context/AppContext';
import { ROLE_CONFIG } from '../../data/roleConfig';
import PageTitle from '../../components/common/PageTitle';
import EmptyMessage from '../../components/common/EmptyMessage';

export default function ReceptionistClients() {
  const { currentRole } = useContext(AppContext);
  const config = ROLE_CONFIG[currentRole];

  return (
    <div>
      <PageTitle eyebrow="Anagrafiche" title="Gestione Clienti" />

      <div className="panel section-spaced-sm">
        {config?.allClients?.length > 0 ? (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Cliente</th>
                  <th>Contatti</th>
                  <th>Animali Associati</th>
                  <th>Azioni</th>
                </tr>
              </thead>
              <tbody>
                {config.allClients.map((client, index) => (
                  <tr key={index}>
                    <td><strong>{client.nominativo}</strong></td>
                    <td>{client.email}</td>
                    <td>{client.animaliString}</td>
                    <td>
                      <button className="btn btn-outline btn-sm" onClick={() => window.alert('Apertura scheda anagrafica')}>
                        Modifica Dati
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <EmptyMessage>Nessun utente cliente registrato nell'anagrafica.</EmptyMessage>
        )}
      </div>
    </div>
  );
}
