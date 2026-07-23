import { useEffect, useState } from 'react';
import PageTitle from '../../components/common/PageTitle';
import EmptyMessage from '../../components/common/EmptyMessage';
import { fetchClients } from '../../services/userApi';

function getClientName(client) {
  return [client.nome, client.cognome].filter(Boolean).join(' ') || client.email || 'N/D';
}

export default function ReceptionistClients() {
  const [clients, setClients] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [loadError, setLoadError] = useState('');

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
                  <th>Animali Associati</th>
                  <th>Azioni</th>
                </tr>
              </thead>
              <tbody>
                {clients.map((client, index) => (
                  <tr key={index}>
                    <td><strong>{getClientName(client)}</strong></td>
                    <td>{client.email}</td>
                    <td>{client.animaliString ?? 'Da collegare'}</td>
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
        ) : !isLoading && (
          <EmptyMessage>Nessun utente cliente registrato nell'anagrafica.</EmptyMessage>
        )}
      </div>
    </div>
  );
}
