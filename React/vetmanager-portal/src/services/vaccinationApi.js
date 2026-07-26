import { apiFetch, apiFetchWithPayload, readApiError } from './apiClient';

const VACCINATIONS_API_BASE = '/api/vaccinazioni';
const VACCINE_TYPES_API_BASE = '/api/tipi-vaccino';

const VACCINATION_STATUS = {
  ACTIVE: 'Attivo',
  EXPIRING: 'In scadenza',
  EXPIRED: 'Scaduto',
};

function toDate(value) {
  if (!value) {
    return null;
  }

  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? null : date;
}

function toDateInputValue(value) {
  return value ? String(value).slice(0, 10) : '';
}

function toLocalDateValue(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');

  return `${year}-${month}-${day}`;
}

function addMonths(date, months) {
  const endDate = new Date(date);
  const originalDay = endDate.getDate();

  endDate.setMonth(endDate.getMonth() + Number(months || 0));

  if (endDate.getDate() < originalDay) {
    endDate.setDate(0);
  }

  return endDate;
}

function getVaccinationStatus(endDate) {
  if (!endDate) {
    return VACCINATION_STATUS.EXPIRED;
  }

  const today = new Date();
  today.setHours(0, 0, 0, 0);

  const normalizedEndDate = new Date(endDate);
  normalizedEndDate.setHours(0, 0, 0, 0);

  if (normalizedEndDate < today) {
    return VACCINATION_STATUS.EXPIRED;
  }

  const oneMonthFromToday = new Date(today);
  oneMonthFromToday.setMonth(oneMonthFromToday.getMonth() + 1);

  return normalizedEndDate < oneMonthFromToday
    ? VACCINATION_STATUS.EXPIRING
    : VACCINATION_STATUS.ACTIVE;
}

function normalizeVaccineType(vaccineType) {
  if (!vaccineType) {
    return null;
  }

  return {
    id: vaccineType.id,
    nome: vaccineType.tipologia ?? vaccineType.nome ?? '',
    durataMesi: vaccineType.durata ?? vaccineType.durataMesi ?? 0,
    note: vaccineType.note ?? '',
    raw: vaccineType,
  };
}

export function normalizeVaccination(vaccination) {
  if (!vaccination) {
    return null;
  }

  const vaccinationDate = toDate(vaccination.dataVaccinazione);
  const durationMonths = Number(vaccination.durataMesi ?? vaccination.idTipoVaccino?.durata ?? 0);
  const endDate = vaccinationDate && durationMonths > 0 ? addMonths(vaccinationDate, durationMonths) : null;

  return {
    id: vaccination.id,
    tipoVaccinoId: vaccination.tipoVaccinoId ?? vaccination.idTipoVaccino?.id ?? '',
    nomeVaccino: vaccination.tipoVaccino ?? vaccination.idTipoVaccino?.tipologia ?? '',
    durataMesi: durationMonths,
    animaleId: vaccination.animaleId ?? vaccination.idAnimale?.id ?? '',
    dataVaccinazione: vaccination.dataVaccinazione ?? '',
    dataVaccinazioneValue: toDateInputValue(vaccination.dataVaccinazione),
    dataScadenza: endDate ? toLocalDateValue(endDate) : '',
    dataScadenzaValue: endDate ? toLocalDateValue(endDate) : '',
    stato: getVaccinationStatus(endDate),
    lotto: vaccination.lotto ?? '',
    raw: vaccination,
  };
}

function normalizeVaccinationList(vaccinations) {
  const vaccinationList = Array.isArray(vaccinations) ? vaccinations : [vaccinations];
  return vaccinationList.map(normalizeVaccination).filter(Boolean);
}

function buildVaccinationPayload({ animaleId, tipoVaccinoId, dataVaccinazione, lotto }) {
  return {
    animaleId: Number(animaleId),
    tipoVaccinoId: Number(tipoVaccinoId),
    dataVaccinazione: `${dataVaccinazione}T00:00:00`,
    lotto: lotto?.trim() || null,
  };
}

export async function fetchVaccineTypes() {
  const response = await apiFetch(VACCINE_TYPES_API_BASE);

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile recuperare i tipi di vaccino.');
    throw new Error(errorMessage);
  }

  const vaccineTypes = await response.json();
  const vaccineTypeList = Array.isArray(vaccineTypes) ? vaccineTypes : [vaccineTypes];

  return vaccineTypeList
    .filter((vaccineType) => !vaccineType.isDeleted)
    .map(normalizeVaccineType)
    .filter(Boolean);
}

export async function fetchVaccinationsByAnimal(animalId) {
  const response = await apiFetch(`${VACCINATIONS_API_BASE}/animale/${animalId}`);

  if (!response.ok) {
    const errorMessage = await readApiError(response, "Impossibile recuperare le vaccinazioni dell'animale.");
    throw new Error(errorMessage);
  }

  return normalizeVaccinationList(await response.json());
}

export async function createVaccination(payload) {
  const response = await apiFetchWithPayload(VACCINATIONS_API_BASE, [buildVaccinationPayload(payload)]);

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile aggiungere la vaccinazione.');
    throw new Error(errorMessage);
  }

  return normalizeVaccination(await response.json());
}

export async function renewVaccination(vaccination, renewalDate) {
  const response = await apiFetchWithPayload(`${VACCINATIONS_API_BASE}/${vaccination.id}`, [
    buildVaccinationPayload({
      animaleId: vaccination.animaleId,
      tipoVaccinoId: vaccination.tipoVaccinoId,
      dataVaccinazione: renewalDate,
      lotto: vaccination.lotto,
    }),
  ], {
    method: 'PUT',
  });

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile rinnovare la vaccinazione.');
    throw new Error(errorMessage);
  }

  return normalizeVaccination(await response.json());
}

export function getTodayDateValue() {
  return toLocalDateValue(new Date());
}
