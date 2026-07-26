import { useMemo, useState } from 'react';
import { payVisit } from '../../services/paymentApi';
import './PaymentVisitCards.css';

const RECEPTIONIST_ROLES = new Set(['receptionist', 'RECEPTIONIST']);
const CLIENT_ROLES = new Set(['client', 'cliente', 'CLIENTE']);
const ELECTRONIC_PAYMENT = 'ELETTRONICO';
const CASH_PAYMENT = 'CONTANTI';

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

function getRoleType(role) {
  if (RECEPTIONIST_ROLES.has(role)) {
    return 'receptionist';
  }

  if (CLIENT_ROLES.has(role)) {
    return 'client';
  }

  return 'other';
}

function PaymentRecap({ visit, paymentType }) {
  return (
    <dl className="unpaid-visit-card__recap">
      <div>
        <dt>Animale</dt>
        <dd>{visit.animalName || 'N/D'}</dd>
      </div>
      <div>
        <dt>Proprietario</dt>
        <dd>{visit.ownerName || 'N/D'}</dd>
      </div>
      <div>
        <dt>Visita</dt>
        <dd>{visit.visitTypeName || 'Visita veterinaria'}</dd>
      </div>
      <div>
        <dt>Data visita</dt>
        <dd>{formatDate(visit.visitDate)}</dd>
      </div>
      <div>
        <dt>Metodo</dt>
        <dd>{paymentType || 'Da selezionare'}</dd>
      </div>
      <div>
        <dt>Totale</dt>
        <dd>{formatCurrency(visit.totalAmount)}</dd>
      </div>
    </dl>
  );
}

export default function UnpaidVisitCard({ visit, currentRole, onPaid, onVisitDetails }) {
  const [dialogStep, setDialogStep] = useState('closed');
  const [paymentType, setPaymentType] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState('');
  const roleType = useMemo(() => getRoleType(currentRole), [currentRole]);
  const isDialogOpen = dialogStep !== 'closed';

  function closeDialog() {
    if (isSubmitting) {
      return;
    }

    setDialogStep('closed');
    setPaymentType('');
    setSubmitError('');
  }

  function openPaymentDialog() {
    setSubmitError('');

    if (roleType === 'receptionist') {
      setDialogStep('method');
      return;
    }

    setPaymentType(ELECTRONIC_PAYMENT);
    setDialogStep('confirm');
  }

  function selectPaymentType(type) {
    setPaymentType(type);
    setDialogStep('confirm');
  }

  function goBackFromConfirm() {
    setSubmitError('');

    if (roleType === 'receptionist') {
      setDialogStep('method');
      return;
    }

    closeDialog();
  }

  async function handleConfirmPayment() {
    setIsSubmitting(true);
    setSubmitError('');

    try {
      const result = await payVisit(visit, paymentType);
      onPaid?.(result.visit ?? visit);
      closeDialog();
    } catch (error) {
      setSubmitError(error.message);
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <>
      <article className="unpaid-visit-card">
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

        <div className="unpaid-visit-card__actions" aria-label="Azioni pagamento">
          <button type="button" className="btn btn-primary btn-sm" onClick={openPaymentDialog}>
            Paga Ora
          </button>
          <button type="button" className="btn btn-outline btn-sm" onClick={() => onVisitDetails?.(visit)}>
            Scheda Visita
          </button>
        </div>
      </article>

      {isDialogOpen && (
        <div className="payment-dialog" role="presentation">
          <section
            className="payment-dialog__panel"
            role="dialog"
            aria-modal="true"
            aria-labelledby={`payment-dialog-title-${visit.id}`}
          >
            {dialogStep === 'method' ? (
              <>
                <h2 id={`payment-dialog-title-${visit.id}`}>Pagamento visita</h2>
                <p className="payment-dialog__statement">Selezionare metodo di pagamento:</p>
                <div className="payment-dialog__actions">
                  <button type="button" className="btn btn-outline" onClick={closeDialog}>
                    Indietro
                  </button>
                  <button type="button" className="btn btn-secondary" onClick={() => selectPaymentType(ELECTRONIC_PAYMENT)}>
                    Elettronico
                  </button>
                  <button type="button" className="btn btn-secondary" onClick={() => selectPaymentType(CASH_PAYMENT)}>
                    Contanti
                  </button>
                </div>
              </>
            ) : (
              <>
                <h2 id={`payment-dialog-title-${visit.id}`}>Conferma pagamento</h2>
                <PaymentRecap visit={visit} paymentType={paymentType} />

                {submitError && (
                  <p className="form-status form-status--error">
                    {submitError}
                  </p>
                )}

                <div className="payment-dialog__actions payment-dialog__actions--end">
                  <button type="button" className="btn btn-outline" onClick={goBackFromConfirm} disabled={isSubmitting}>
                    Indietro
                  </button>
                  <button type="button" className="btn btn-primary" onClick={handleConfirmPayment} disabled={isSubmitting}>
                    {isSubmitting ? 'Conferma in corso...' : 'Conferma'}
                  </button>
                </div>
              </>
            )}
          </section>
        </div>
      )}
    </>
  );
}

export function UnpaidVisitCardList({ visits, currentRole, onPaid, onVisitDetails }) {
  return (
    <div className="unpaid-visit-card-list">
      {visits.map((visit, index) => (
        <UnpaidVisitCard
          key={visit.id ?? index}
          visit={visit}
          currentRole={currentRole}
          onPaid={onPaid}
          onVisitDetails={onVisitDetails}
        />
      ))}
    </div>
  );
}
