import { apiFetch, apiFetchWithPayload, readApiError } from './apiClient';

const VISITS_API_BASE = '/api/visite';
const PAYMENTS_API_BASE = '/api/pagamenti';
const UNPAID_VISITS_ENDPOINT = `${VISITS_API_BASE}/ottieniNonPagate`;
const PAID_VISITS_ENDPOINT = `${VISITS_API_BASE}/ottieniPagate`;

function getFullName(person) {
  return [person?.nome, person?.cognome].filter(Boolean).join(' ');
}

function toNumber(value) {
  const parsedValue = Number(value);
  return Number.isFinite(parsedValue) ? parsedValue : 0;
}

function getVisitOwner(visit) {
  return visit.cliente ?? visit.client ?? visit.proprietario ?? visit.animale?.utente ?? visit.utente ?? {};
}

function getVisitPrice(visit) {
  return (
    visit.totalImport ??
    visit.importoTotale ??
    visit.importo ??
    visit.pagamento?.importoTotale ??
    visit.tipoVisita?.prezzo ??
    0
  );
}

export function normalizeUnpaidVisit(visit) {
  const owner = getVisitOwner(visit);
  const visitType = visit.tipoVisita ?? {};
  const payment = visit.pagamento ?? {};
  const receiptReference = payment.riferimentoFile ?? payment.fileReference ?? payment.receipt ?? {};

  return {
    id: visit.id ?? visit.visitaId,
    animalName: visit.animalName ?? visit.animaleNome ?? visit.animale?.nome ?? '',
    ownerName: visit.ownerName ?? visit.clienteNome ?? getFullName(owner),
    ownerId: owner?.id,
    visitDate: visit.visitDate ?? visit.dataVisita ?? visit.date ?? '',
    visitTypeName: visit.visitTypeName ?? visit.tipoVisitaNome ?? visitType.nome ?? '',
    totalAmount: toNumber(getVisitPrice(visit)),
    paymentId: payment.id,
    paymentDate: payment.data,
    paymentType: payment.tipoPagamento,
    receiptReferenceId: receiptReference.id,
    receiptFileName: receiptReference.originalFileName ?? receiptReference.storedFileName ?? '',
    receiptStoragePath: receiptReference.storagePath ?? '',
    raw: visit,
  };
}

export async function fetchUnpaidVisits() {
  const response = await apiFetch(UNPAID_VISITS_ENDPOINT);

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile recuperare le visite non pagate.');
    throw new Error(errorMessage || 'Impossibile recuperare le visite non pagate.');
  }

  const visits = await response.json();
  const visitList = Array.isArray(visits) ? visits : [visits];

  return visitList.map(normalizeUnpaidVisit);
}

export async function fetchPaidVisits() {
  const response = await apiFetch(PAID_VISITS_ENDPOINT);

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile recuperare le visite pagate.');
    throw new Error(errorMessage || 'Impossibile recuperare le visite pagate.');
  }

  const visits = await response.json();
  const visitList = Array.isArray(visits) ? visits : [visits];

  return visitList.map(normalizeUnpaidVisit);
}

export function getReceiptDownloadTarget(visit) {
  const storagePath = visit.receiptStoragePath;

  if (visit.receiptReferenceId) {
    return `/api/file-reference/${visit.receiptReferenceId}/download`;
  }

  if (storagePath?.startsWith('http')) {
    return storagePath;
  }

  return '';
}

export async function downloadReceipt(visit) {
  const receiptTarget = getReceiptDownloadTarget(visit);

  if (!receiptTarget) {
    throw new Error('Ricevuta non disponibile per questa visita.');
  }

  if (receiptTarget.startsWith('http')) {
    window.open(receiptTarget, '_blank', 'noopener,noreferrer');
    return;
  }

  const response = await apiFetch(receiptTarget);

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile scaricare la ricevuta.');
    throw new Error(errorMessage || 'Impossibile scaricare la ricevuta.');
  }

  const receiptBlob = await response.blob();
  const receiptUrl = window.URL.createObjectURL(receiptBlob);
  const downloadLink = document.createElement('a');

  downloadLink.href = receiptUrl;
  downloadLink.download = visit.receiptFileName || 'ricevuta.pdf';
  document.body.appendChild(downloadLink);
  downloadLink.click();
  downloadLink.remove();
  window.URL.revokeObjectURL(receiptUrl);
}

export async function createPayment(visit, paymentType) {
  const payload = {
    data: new Date().toISOString().slice(0, 10),
    tipoPagamento: paymentType,
    importoTotale: visit.totalAmount,
    isDeleted: false,
  };

  if (visit.ownerId) {
    payload.utente = { id: visit.ownerId };
  }

  const response = await apiFetchWithPayload(`${PAYMENTS_API_BASE}/crea`, [payload]);

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile creare il pagamento.');
    throw new Error(errorMessage || 'Impossibile creare il pagamento.');
  }

  return response.json();
}

export async function attachPaymentToVisit(visitId, payment) {
  const response = await apiFetch(`${VISITS_API_BASE}/aggiornaPagato/${visitId}?pagamento=${payment.id}`, {
    method: 'PATCH',
  });

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile associare il pagamento alla visita.');
    throw new Error(errorMessage || 'Impossibile associare il pagamento alla visita.');
  }

  return response.json();
}

export async function payVisit(visit, paymentType) {
  const payment = await createPayment(visit, paymentType);
  const updatedVisit = await attachPaymentToVisit(visit.id, payment);

  return {
    payment,
    updatedVisit,
    visit: normalizeUnpaidVisit(updatedVisit),
  };
}
