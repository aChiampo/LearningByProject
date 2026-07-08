import { useContext } from 'react';
import { AppContext } from '../../context/AppContext';
import { ROLE_CONFIG } from '../../data/roleConfig';
import PageTitle from '../../components/common/PageTitle';
import EmptyMessage from '../../components/common/EmptyMessage';

export default function ReceptionistAppointments() {
  const { currentRole } = useContext(AppContext);
  const config = ROLE_CONFIG[currentRole];

  return (
    <div>
      <PageTitle eyebrow="Planning" title="Gestione Appuntamenti" />

      <div className="panel section-spaced-sm">
        <div className="toolbar-row">
          <h2>Calendario Generale</h2>
          <button className="btn btn-primary btn-sm" onClick={() => window.alert('Apertura popup per inserire un appuntamento sul posto')}>
            + Nuovo Appuntamento
          </button>
        </div>

        {config?.allAppointments?.length > 0 ? (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Data/Ora</th>
                  <th>Paziente (Proprietario)</th>
                  <th>Medico</th>
                  <th>Stato</th>
                  <th>Azioni</th>
                </tr>
              </thead>
              <tbody>
                {config.allAppointments.map((appointment, index) => (
                  <tr key={index}>
                    <td>{appointment.dataOra}</td>
                    <td>{appointment.pazienteNome} ({appointment.proprietarioNome})</td>
                    <td>{appointment.medicoNome}</td>
                    <td><span className="badge">{appointment.stato}</span></td>
                    <td>
                      <div className="actions-row">
                        <button className="btn btn-outline btn-sm" onClick={() => window.alert('Modifica orario/medico')}>Modifica</button>
                        <button className="btn btn-danger btn-sm" onClick={() => window.alert('Appuntamento eliminato')}>Cancella</button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <EmptyMessage>Nessun appuntamento registrato a sistema.</EmptyMessage>
        )}
      </div>
    </div>
  );
}
