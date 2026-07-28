import { useEffect, useState } from 'react';
import PageTitle from '../../components/common/PageTitle';
import EmptyMessage from '../../components/common/EmptyMessage';
import UserProfile from '../../components/common/UserProfile';
import { fetchClients } from '../../services/userApi';

function getClientName(client) {
  return [client.nome, client.cognome].filter(Boolean).join(' ') || client.email || 'N/D';
}

export default function ReceptionistClients() {
  const [clients, setClients] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [loadError, setLoadError] = useState('');
  const [clientToEdit, setClientToEdit] = useState(null);

  useEffect(() => {
    let isMounted = true;

    async function loadClients() {
      setIsLoading(true);
      setLoadError('');

      try {
        const clientList = await fetchClients();

        if (isMounted) {
          setClients(clientList);
        }
      } catch (error) {
        if (isMounted) {
          setLoadError(error.message);
          setClients([]);
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    loadClients();

    return () => {
      isMounted = false;
    };
  }, []);

  function handleClientUpdated(updatedClient) {
    setClients((currentClients) => (
      currentClients.map((client) => (
        client.id === updatedClient.id ? updatedClient : client
      ))
    ));
  }

  return (
    <div>
      <PageTitle eyebrow="Anagrafiche" title="Gestione Clienti" />

      <div className="panel section-spaced-sm">
        {isLoading && <p className="muted-text">Caricamento clienti...</p>}

        {loadError && (
          <p className="form-status form-status--error">
            {loadError}
          </p>
        )}

        {!isLoading && clients.length > 0 ? (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Cliente</th>
                  <th>Contatti</th>
                  <th>Telefono</th>
                  <th>Azioni</th>
                </tr>
              </thead>
              <tbody>
                {clients.map((client) => (
                  <tr key={client.id}>
                    <td><strong>{getClientName(client)}</strong></td>
                    <td>{client.email}</td>
                    <td>{client.telefono || 'Non disponibile'}</td>
                    <td>
                      <button className="btn btn-outline btn-sm" onClick={() => setClientToEdit(client)}>
                        Modifica dati
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : !isLoading && (
          <EmptyMessage>Nessun utente cliente registrato nell'anagrafica.</EmptyMessage>
        )}
      </div>

      {clientToEdit && (
        <UserProfile
          profileUser={clientToEdit}
          fallbackName={getClientName(clientToEdit)}
          description="Scheda cliente"
          dialogMode
          dialogTitle="Modifica Profilo Cliente"
          onClose={() => setClientToEdit(null)}
          onProfileUpdated={handleClientUpdated}
        />
      )}
    </div>
  );
}
