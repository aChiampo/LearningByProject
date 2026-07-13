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
  const response = await fetch(UNPAID_VISITS_ENDPOINT);

  if (!response.ok) {
    const errorMessage = await response.text();
    throw new Error(errorMessage || 'Impossibile recuperare le visite non pagate.');
  }

  const visits = await response.json();
  const visitList = Array.isArray(visits) ? visits : [visits];

  return visitList.map(normalizeUnpaidVisit);
}

export async function fetchPaidVisits() {
  const response = await fetch(PAID_VISITS_ENDPOINT);

  if (!response.ok) {
    const errorMessage = await response.text();
    throw new Error(errorMessage || 'Impossibile recuperare le visite pagate.');
  }

  const visits = await response.json();
  const visitList = Array.isArray(visits) ? visits : [visits];

  return visitList.map(normalizeUnpaidVisit);
}

export function getReceiptDownloadTarget(visit) {
  const storagePath = visit.receiptStoragePath;

  if (storagePath?.startsWith('http') || storagePath?.startsWith('/')) {
    return storagePath;
  }

  if (visit.receiptReferenceId) {
    return `/api/file-reference/${visit.receiptReferenceId}/download`;
  }

  return '';
}

export async function createPayment(visit, paymentType) {
  const payload = {
    data: new Date().toISOString().slice(0, 10),
    tipoPagamento: paymentType,
    stato: 'PAGATO',
    importoTotale: visit.totalAmount,
    isDeleted: false,
  };

  if (visit.ownerId) {
    payload.utente = { id: visit.ownerId };
  }

  const response = await fetch(`${PAYMENTS_API_BASE}/crea`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    const errorMessage = await response.text();
    throw new Error(errorMessage || 'Impossibile creare il pagamento.');
  }

  return response.json();
}

export async function attachPaymentToVisit(visitId, payment) {
  const response = await fetch(`${VISITS_API_BASE}/aggiornaPagato/${visitId}?pagamento=${payment.id}`, {
    method: 'PATCH',
  });

  if (!response.ok) {
    const errorMessage = await response.text();
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
  };
}
