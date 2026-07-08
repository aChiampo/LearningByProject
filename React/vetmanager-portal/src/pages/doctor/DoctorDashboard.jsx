import { useContext } from 'react';
import { AppContext } from '../../context/AppContext';
import { ROLE_CONFIG } from '../../data/roleConfig';
import PageTitle from '../../components/common/PageTitle';
import EmptyMessage from '../../components/common/EmptyMessage';

export default function DoctorDashboard() {
  const { currentRole } = useContext(AppContext);
  const config = ROLE_CONFIG[currentRole];

  return (
    <div>
      <PageTitle eyebrow="Area Professionale" title="Dashboard Clinica" />

      <div className="panel section-spaced-sm">
        <h2>Agenda Visite di Oggi</h2>
        <p className="muted-text">Controllo appuntamenti attivi</p>

        {config?.todayAppointments?.length > 0 ? (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Orario</th>
                  <th>Paziente</th>
                  <th>Proprietario</th>
                  <th>Motivo</th>
                  <th>Azioni</th>
                </tr>
              </thead>
              <tbody>
                {config.todayAppointments.map((visit, index) => (
                  <tr key={index}>
                    <td><strong>{visit.orario}</strong></td>
                    <td>{visit.pazienteNome} ({visit.pazienteRazza})</td>
                    <td>{visit.proprietario}</td>
                    <td>{visit.motivo}</td>
                    <td>
                      <button className="btn btn-primary btn-sm" onClick={() => window.alert(`Apertura cartella clinica di ${visit.pazienteNome}`)}>
                        Visita
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <EmptyMessage>Nessun appuntamento programmato per la giornata di oggi.</EmptyMessage>
        )}
      </div>
    </div>
  );
}
