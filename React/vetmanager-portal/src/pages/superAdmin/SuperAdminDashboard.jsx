import PageTitle from '../../components/common/PageTitle';

export default function SuperAdminDashboard() {
  return (
    <div>
      <PageTitle eyebrow="Amministrazione di Sistema" title="Stato del Portale" />

      <div className="panel section-spaced-sm">
        <h2>Monitoraggio Moduli Applicativi</h2>
        <p className="muted-text">Verifica dello stato di prontezza delle aree del mockup</p>

        <div className="status-stack">
          <div className="status-line">
            <span><strong>Area Pubblica (Homepage & Router)</strong></span>
            <span className="status-complete">Completato (React)</span>
          </div>
          <div className="status-line">
            <span><strong>Area Cliente (Thor & Luna)</strong></span>
            <span className="status-complete">Completato (React)</span>
          </div>
          <div className="status-line">
            <span><strong>Area Veterinario (Dott. Zampetti)</strong></span>
            <span className="status-complete">Completato (React)</span>
          </div>
          <div className="status-line">
            <span><strong>Area Reception (Giulia Ferri)</strong></span>
            <span className="status-complete">Completato (React)</span>
          </div>
        </div>
      </div>
    </div>
  );
}
