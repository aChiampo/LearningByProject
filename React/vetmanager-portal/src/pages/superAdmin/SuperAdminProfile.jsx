import PageTitle from '../../components/common/PageTitle';

export default function SuperAdminProfile() {
  return (
    <div>
      <PageTitle eyebrow="Configurazione" title="Impostazioni di Sistema" />
      <div className="panel panel-narrow">
        <h2>Parametri Globali</h2>
        <p className="muted-text">Versione Mockup: v2.0-React (Vite)</p>
        <div className="config-box">
          <p><strong>Database:</strong> Simulato in memoria locale</p>
          <p><strong>Routing:</strong> Gestito tramite React Router</p>
        </div>
      </div>
    </div>
  );
}
