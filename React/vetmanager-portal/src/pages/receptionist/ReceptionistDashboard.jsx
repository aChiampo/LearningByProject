import { useContext } from 'react';
import { AppContext } from '../../context/AppContext';
import { ROLE_CONFIG } from '../../data/roleConfig';
import PageTitle from '../../components/common/PageTitle';

export default function ReceptionistDashboard() {
  const { currentRole } = useContext(AppContext);
  const config = ROLE_CONFIG[currentRole];

  return (
    <div>
      <PageTitle eyebrow="Gestione Studio" title="Dashboard Reception" />

      <div className="stats-grid">
        <div className="panel stat-panel">
          <strong className="stat-value stat-value--primary">{config?.stats?.todayAppointmentsCount || 0}</strong>
          <p>Appuntamenti Oggi</p>
        </div>
        <div className="panel stat-panel">
          <strong className="stat-value stat-value--secondary">{config?.stats?.newClientsCount || 0}</strong>
          <p>Nuovi Clienti</p>
        </div>
        <div className="panel stat-panel">
          <strong className="stat-value stat-value--danger">Euro {config?.stats?.pendingAmount || 0}</strong>
          <p>In Sospeso</p>
        </div>
      </div>
    </div>
  );
}
