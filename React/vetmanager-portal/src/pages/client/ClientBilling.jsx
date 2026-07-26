import { useContext, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppContext } from '../../context/AppContext';
import PageTitle from '../../components/common/PageTitle';
import EmptyMessage from '../../components/common/EmptyMessage';
import { UnpaidVisitCardList } from '../../components/payments/UnpaidVisitCard';
import { PaidVisitCardList } from '../../components/payments/PaidVisitCard';
import { fetchPaidVisits, fetchUnpaidVisits } from '../../services/paymentApi';

export default function ClientBilling() {
  const navigate = useNavigate();
  const { currentRole } = useContext(AppContext);
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
    setPaidVisits((currentVisits) => [
      paidVisit,
      ...currentVisits.filter((visit) => visit.id !== paidVisit.id),
    ]);
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

      {!isLoading && (unpaidVisits.length > 0 || paidVisits.length > 0) ? (
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
