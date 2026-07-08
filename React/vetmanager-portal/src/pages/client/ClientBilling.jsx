import { useContext } from 'react';
import { AppContext } from '../../context/AppContext';
import { ROLE_CONFIG } from '../../data/roleConfig';
import PageTitle from '../../components/common/PageTitle';
import EmptyMessage from '../../components/common/EmptyMessage';

export default function ClientBilling() {
  const { currentRole } = useContext(AppContext);
  const config = ROLE_CONFIG[currentRole];
  const pendingInvoices = config?.billing?.filter((invoice) => invoice.status === 'Da Saldare') ?? [];

  return (
    <div>
      <PageTitle eyebrow="Amministrazione" title="I tuoi Pagamenti" />

      {config?.billing?.length > 0 ? (
        <>
          <section className="section-block">
            <h2 className="danger-heading">Da Saldare</h2>
            {pendingInvoices.map((invoice, index) => (
              <div key={index} className="panel invoice-row invoice-row--danger">
                <div className="invoice-main">
                  <strong>{invoice.description}</strong>
                  <p>Data prestazione: {invoice.date}</p>
                </div>
                <div className="invoice-summary">
                  <span>Euro {invoice.amount}</span>
                  <button className="btn btn-primary btn-sm" onClick={() => window.alert('Simulazione pagamento riuscita!')}>
                    Paga Ora
                  </button>
                </div>
              </div>
            ))}
          </section>

          <section className="section-block section-spaced">
            <h2>Storico Ricevute e Fatture</h2>
            <div className="panel table-responsive">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Numero</th>
                    <th>Data</th>
                    <th>Descrizione</th>
                    <th>Importo</th>
                    <th>Stato</th>
                  </tr>
                </thead>
                <tbody>
                  {config.billing.map((invoice, index) => (
                    <tr key={index}>
                      <td>{invoice.id}</td>
                      <td>{invoice.date}</td>
                      <td>{invoice.description}</td>
                      <td><strong>Euro {invoice.amount}</strong></td>
                      <td><span className={`badge ${invoice.status === 'Saldato' ? 'badge-neutral' : 'badge-danger'}`}>{invoice.status}</span></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        </>
      ) : (
        <EmptyMessage>Nessuna fattura o ricevuta presente nel tuo storico contabile.</EmptyMessage>
      )}
    </div>
  );
}
