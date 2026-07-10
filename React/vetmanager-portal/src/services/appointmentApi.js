const VISITS_API_BASE = '/api/visite';
const VISIT_PARAMS_ENDPOINT = `${VISITS_API_BASE}/params`;

function getFullName(person) {
  return [person?.nome, person?.cognome].filter(Boolean).join(' ');
}

function formatAppointmentHour(dateTime) {
  if (!dateTime) {
    return '';
  }

  const date = new Date(dateTime);

  if (Number.isNaN(date.getTime())) {
    return '';
  }

  return new Intl.DateTimeFormat('it-IT', {
    hour: '2-digit',
    minute: '2-digit'
  }).format(date);
}

function normalizeVisitToAppointment(visit) {
  const client = visit.cliente ?? visit.client ?? visit.proprietario ?? visit.animale?.utente ?? {};
  const doctor = visit.veterinario ?? visit.doctor ?? visit.tipoVisita?.dottore ?? {};

  return {
    id: visit.id,
    animalName: visit.animalName ?? visit.animaleNome ?? visit.animale?.nome ?? '',
    nome: visit.nome ?? client.nome ?? '',
    cognome: visit.cognome ?? client.cognome ?? '',
    visitType: visit.visitType ?? visit.tipoVisitaNome ?? visit.tipoVisita?.nome ?? '',
    appointmentHour: visit.appointmentHour ?? formatAppointmentHour(visit.dataVisita ?? visit.date),
    doctorName: visit.doctorName ?? getFullName(doctor),
    raw: visit
  };
}

export async function fetchAppointments(params = {}) {
  const response = await fetch(VISIT_PARAMS_ENDPOINT, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(params)
  });

  if (!response.ok) {
    const errorMessage = await response.text();
    throw new Error(errorMessage || 'Impossibile recuperare gli appuntamenti.');
  }

  const visits = await response.json();
  const visitList = Array.isArray(visits) ? visits : [visits];

  return visitList.map(normalizeVisitToAppointment);
}
