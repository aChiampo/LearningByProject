import { useContext, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppContext } from '../../context/AppContext';
import PageTitle from '../../components/common/PageTitle';
import EmptyMessage from '../../components/common/EmptyMessage';
import { UnpaidVisitCardList } from '../../components/payments/UnpaidVisitCard';
import { PaidVisitCardList } from '../../components/payments/PaidVisitCard';
import { fetchPaidVisits, fetchUnpaidVisits } from '../../services/paymentApi';

export default function ReceptionistPayments() {
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
  }

  function handleVisitDetails(visit) {
    navigate(`/receptionist/appointments?visitaId=${visit.id}`);
  }

  return (
    <div>
      <PageTitle eyebrow="Cassa" title="Gestione Pagamenti" />

      <div className="panel section-spaced-sm">
        <h2>Pendenze Attive</h2>

        {isLoading && <p className="muted-text">Caricamento pagamenti...</p>}

        {loadError && (
          <p className="form-status form-status--error">
            {loadError}
          </p>
        )}

        {!isLoading && unpaidVisits.length > 0 && (
          <UnpaidVisitCardList
            visits={unpaidVisits}
            currentRole={currentRole}
            onPaid={handlePaidVisit}
            onVisitDetails={handleVisitDetails}
          />
        )}

        {!isLoading && unpaidVisits.length === 0 && (
          <EmptyMessage>Ottimo lavoro! Non ci sono pagamenti in sospeso da riscuotere.</EmptyMessage>
        )}
      </div>

      <div className="panel section-spaced-sm">
        <h2>Pagamenti Registrati</h2>

        {isLoading && <p className="muted-text">Caricamento ricevute...</p>}

        {!isLoading && paidVisits.length > 0 && (
          <PaidVisitCardList
            visits={paidVisits}
            onVisitDetails={handleVisitDetails}
          />
        )}

        {!isLoading && paidVisits.length === 0 && (
          <EmptyMessage>Nessuna visita pagata presente nello storico.</EmptyMessage>
        )}
      </div>
    </div>
  );
}
