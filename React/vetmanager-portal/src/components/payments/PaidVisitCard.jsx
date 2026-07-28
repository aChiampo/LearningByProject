import { downloadReceipt, getReceiptDownloadTarget } from '../../services/paymentApi';
import './PaymentVisitCards.css';

function formatCurrency(value) {
  return new Intl.NumberFormat('it-IT', {
    style: 'currency',
    currency: 'EUR',
  }).format(value ?? 0);
}

function formatDate(value) {
  if (!value) {
    return 'Data non disponibile';
  }

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return new Intl.DateTimeFormat('it-IT', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(date);
}

export default function PaidVisitCard({ visit, onVisitDetails }) {
  const receiptTarget = getReceiptDownloadTarget(visit);

  async function handleReceiptDownload() {
    if (!receiptTarget) {
      window.alert('Ricevuta non disponibile per questa visita.');
      return;
    }

    try {
      await downloadReceipt(visit);
    } catch (error) {
      window.alert(error.message || 'Impossibile scaricare la ricevuta.');
    }
  }

  return (
    <article className="unpaid-visit-card unpaid-visit-card--paid">
      <div className="unpaid-visit-card__main">
        <div>
          <p className="unpaid-visit-card__label">Animale</p>
          <h3>{visit.animalName || 'Animale non indicato'}</h3>
        </div>

        <div className="unpaid-visit-card__meta">
          <span>Proprietario</span>
          <strong>{visit.ownerName || 'N/D'}</strong>
        </div>

        <div className="unpaid-visit-card__meta">
          <span>Data visita</span>
          <strong>{formatDate(visit.visitDate)}</strong>
        </div>

        <div className="unpaid-visit-card__amount">
          <span>Totale</span>
          <strong>{formatCurrency(visit.totalAmount)}</strong>
        </div>
      </div>

      <div className="unpaid-visit-card__actions" aria-label="Azioni ricevuta">
        <button type="button" className="btn btn-primary btn-sm" onClick={handleReceiptDownload}>
          Scarica Ricevuta
        </button>
        <button type="button" className="btn btn-outline btn-sm" onClick={() => onVisitDetails?.(visit)}>
          Scheda Visita
        </button>
      </div>
    </article>
  );
}

export function PaidVisitCardList({ visits, onVisitDetails }) {
  return (
    <div className="unpaid-visit-card-list">
      {visits.map((visit, index) => (
        <PaidVisitCard
          key={visit.id ?? index}
          visit={visit}
          onVisitDetails={onVisitDetails}
        />
      ))}
    </div>
  );
}
