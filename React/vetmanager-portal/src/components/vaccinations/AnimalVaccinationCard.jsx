function formatDate(value) {
  if (!value) {
    return 'N/D';
  }

  const dateOnlyMatch = String(value).match(/^(\d{4})-(\d{2})-(\d{2})$/);
  const date = dateOnlyMatch
    ? new Date(Number(dateOnlyMatch[1]), Number(dateOnlyMatch[2]) - 1, Number(dateOnlyMatch[3]))
    : new Date(value);

  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return new Intl.DateTimeFormat('it-IT', {
    dateStyle: 'medium',
  }).format(date);
}

function getStatusClassName(status) {
  if (status === 'Scaduto') {
    return 'cartella-medica__vaccination-status--expired';
  }

  if (status === 'In scadenza') {
    return 'cartella-medica__vaccination-status--expiring';
  }

  return 'cartella-medica__vaccination-status--active';
}

function getCardStatusClassName(status) {
  if (status === 'Scaduto') {
    return 'cartella-medica__vaccination-card--expired';
  }

  if (status === 'In scadenza') {
    return 'cartella-medica__vaccination-card--expiring';
  }

  return 'cartella-medica__vaccination-card--active';
}

export default function AnimalVaccinationCard({
  vaccination,
  isRenewing = false,
  onRenew,
  readOnly = false,
}) {
  return (
    <article className={`cartella-medica__vaccination-card ${getCardStatusClassName(vaccination.stato)}`}>
      <div>
        <span className="cartella-medica__vaccination-label">Vaccino</span>
        <strong>{vaccination.nomeVaccino || 'Vaccino non disponibile'}</strong>
      </div>

      <div>
        <span className="cartella-medica__vaccination-label">Data vaccinazione</span>
        <strong>{formatDate(vaccination.dataVaccinazione)}</strong>
      </div>

      <div>
        <span className="cartella-medica__vaccination-label">Scadenza</span>
        <strong>{formatDate(vaccination.dataScadenza)}</strong>
      </div>

      <div>
        <span className="cartella-medica__vaccination-label">Stato</span>
        <span className={`cartella-medica__vaccination-status ${getStatusClassName(vaccination.stato)}`}>
          {vaccination.stato}
        </span>
      </div>

      {!readOnly && (
        <button
          className="btn btn-secondary btn-sm"
          type="button"
          onClick={() => onRenew(vaccination)}
          disabled={isRenewing}
        >
          {isRenewing ? 'Rinnovo...' : 'Rinnova'}
        </button>
      )}
    </article>
  );
}
