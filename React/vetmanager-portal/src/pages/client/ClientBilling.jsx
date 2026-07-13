import { useContext, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppContext } from '../../context/AppContext';
import { ROLE_CONFIG } from '../../data/roleConfig';
import PageTitle from '../../components/common/PageTitle';
import EmptyMessage from '../../components/common/EmptyMessage';
import { UnpaidVisitCardList } from '../../components/payments/UnpaidVisitCard';
import { PaidVisitCardList } from '../../components/payments/PaidVisitCard';
import { fetchPaidVisits, fetchUnpaidVisits } from '../../services/paymentApi';

export default function ClientBilling() {
  const navigate = useNavigate();
  const { currentRole } = useContext(AppContext);
  const config = ROLE_CONFIG[currentRole];
  const fallbackInvoices = useMemo(() => config?.billing ?? [], [config?.billing]);
  const pendingInvoices = fallbackInvoices.filter((invoice) => invoice.status === 'Da Saldare');
  const paidInvoices = fallbackInvoices.filter((invoice) => invoice.status !== 'Da Saldare');
  const [unpaidVisits, setUnpaidVisits] = useState([]);
  const [paidVisits, setPaidVisits] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [loadError, setLoadError] = useState('');

  useEffect(() => {
    let isMounted = true;

    async function loadUnpaidVisits() {
      setIsLoading(true);
      setLoadError('');

      try {
        const [unpaidVisitList, paidVisitList] = await Promise.all([
          fetchUnpaidVisits(),
          fetchPaidVisits(),
        ]);

        if (isMounted) {
          setUnpaidVisits(unpaidVisitList);
          setPaidVisits(paidVisitList);
        }
      } catch (error) {
        if (isMounted) {
          setLoadError(error.message);
          setUnpaidVisits([]);
          setPaidVisits([]);
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    loadUnpaidVisits();

    return () => {
      isMounted = false;
    };
  }, []);

  function handlePaidVisit(paidVisit) {
    setUnpaidVisits((currentVisits) => currentVisits.filter((visit) => visit.id !== paidVisit.id));
  }

  function handleVisitDetails(visit) {
    navigate(`/client/booking?visitaId=${visit.id}`);
  }

  return (
    <div>
      <PageTitle eyebrow="Amministrazione" title="I tuoi Pagamenti" />

      {isLoading && <p className="muted-text">Caricamento pagamenti...</p>}

      {loadError && (
        <p className="form-status form-status--error">
          {loadError}
        </p>
      )}

      {!isLoading && (unpaidVisits.length > 0 || paidVisits.length > 0 || fallbackInvoices.length > 0) ? (
        <>
          <section className="section-block">
            <h2 className="danger-heading">Da Saldare</h2>

            {unpaidVisits.length > 0 ? (
              <UnpaidVisitCardList
                visits={unpaidVisits}
                currentRole={currentRole}
                onPaid={handlePaidVisit}
                onVisitDetails={handleVisitDetails}
              />
            ) : pendingInvoices.length > 0 ? (
              pendingInvoices.map((invoice, index) => (
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
              ))
            ) : (
              <EmptyMessage>Non ci sono visite da saldare.</EmptyMessage>
            )}
          </section>

          <section className="section-block section-spaced">
            <h2>Storico Ricevute e Fatture</h2>
            {paidVisits.length > 0 ? (
              <PaidVisitCardList
                visits={paidVisits}
                onVisitDetails={handleVisitDetails}
              />
            ) : paidInvoices.length > 0 ? (
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
                    {paidInvoices.map((invoice, index) => (
                      <tr key={index}>
                        <td>{invoice.id}</td>
                        <td>{invoice.date}</td>
                        <td>{invoice.description}</td>
                        <td><strong>Euro {invoice.amount}</strong></td>
                        <td><span className="badge badge-neutral">{invoice.status}</span></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <EmptyMessage>Nessuna ricevuta saldata presente nello storico.</EmptyMessage>
            )}
          </section>
        </>
      ) : !isLoading && (
        <EmptyMessage>Nessuna fattura o ricevuta presente nel tuo storico contabile.</EmptyMessage>
      )}
    </div>
  );
}
