import { useState } from 'react';

export default function FirstAppointmentForm({
  backLabel = 'Torna alle opzioni',
  copy = {},
  onBack,
}) {
  const {
    eyebrow = 'Prima visita',
    title = 'Conosciamoci.',
    description = 'Lascia alcune informazioni essenziali. Lo studio potra ricontattarti per concordare il momento piu adatto per il primo appuntamento.',
    formEyebrow = 'Richiesta appuntamento',
    formTitle = 'Parlaci di voi',
    ariaLabel = 'Richiesta primo appuntamento',
  } = copy;

  const [formData, setFormData] = useState({
    owner: '',
    email: '',
    phone: '',
    animal: '',
    species: 'Cane',
    reason: 'Controllo generale',
    notes: '',
  });
  const [status, setStatus] = useState('idle'); // idle | loading | success | error
  const [errorMessage, setErrorMessage] = useState('');

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setStatus('loading');
    setErrorMessage('');

    try {
      const response = await fetch('/api/visite/prima-visita', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          clientName: formData.owner,
          email: formData.email,
          phone: formData.phone,
          petName: formData.animal,
          animalType: formData.species,
          visitReason: formData.reason,
          notes: formData.notes,
        }),
      });

      if (!response.ok) {
        const data = await response.json().catch(() => null);
        throw new Error(data?.message || 'Invio non riuscito. Riprova.');
      }

      setStatus('success');
      setFormData({
        owner: '',
        email: '',
        phone: '',
        animal: '',
        species: 'Cane',
        reason: 'Controllo generale',
        notes: '',
      });
    } catch (error) {
      setStatus('error');
      setErrorMessage(error.message || 'Si è verificato un errore imprevisto.');
    }
  };

  return (
    <section className="dialog-screen home-public-section" id="first-appointment">
      <div className="dialog-backdrop" aria-hidden="true" onClick={onBack}></div>
      <div
        className="dialog-window first-appointment-dialog"
        role="dialog"
        aria-modal="true"
        aria-labelledby="first-appointment-title"
        aria-describedby="first-appointment-description"
      >
        <div className="dialog-copy">
          <button className="login-back login-back-button" type="button" onClick={onBack}>
            {backLabel}
          </button>
          <p className="eyebrow">{eyebrow}</p>
          <h1 id="first-appointment-title">{title}</h1>
          <p id="first-appointment-description">{description}</p>
        </div>

        <form className="login-form public-form" aria-label={ariaLabel} onSubmit={handleSubmit}>
          <div>
            <p className="eyebrow">{formEyebrow}</p>
            <h2>{formTitle}</h2>
          </div>
          <label htmlFor="first-owner">
            Nome e cognome
            <input
              id="first-owner"
              name="owner"
              type="text"
              autoComplete="name"
              placeholder="Il tuo nome"
              value={formData.owner}
              onChange={handleChange}
              required
            />
          </label>
          <div className="form-grid-two">
            <label htmlFor="first-email">
              Email
              <input
                id="first-email"
                name="email"
                type="email"
                autoComplete="email"
                placeholder="nome@esempio.it"
                value={formData.email}
                onChange={handleChange}
                required
              />
            </label>
            <label htmlFor="first-phone">
              Telefono
              <input
                id="first-phone"
                name="phone"
                type="tel"
                autoComplete="tel"
                placeholder="Numero di telefono"
                value={formData.phone}
                onChange={handleChange}
                required
              />
            </label>
          </div>
          <div className="form-grid-two">
            <label htmlFor="first-animal">
              Nome dell'animale
              <input
                id="first-animal"
                name="animal"
                type="text"
                placeholder="Nome"
                value={formData.animal}
                onChange={handleChange}
                required
              />
            </label>
            <label htmlFor="first-species">
              Animale
              <select id="first-species" name="species" value={formData.species} onChange={handleChange}>
                <option>Cane</option>
                <option>Gatto</option>
                <option>Altro piccolo animale</option>
              </select>
            </label>
          </div>
          <label htmlFor="first-reason">
            Motivo della visita
            <select id="first-reason" name="reason" value={formData.reason} onChange={handleChange}>
              <option>Controllo generale</option>
              <option>Vaccinazione</option>
              <option>Consulenza</option>
              <option>Altro</option>
            </select>
          </label>
          <label htmlFor="first-notes">
            Informazioni utili
            <textarea
              id="first-notes"
              name="notes"
              rows="4"
              placeholder="Aggiungi una breve nota, se necessario"
              value={formData.notes}
              onChange={handleChange}
            ></textarea>
          </label>

          {status === 'error' && (
            <p role="alert" className="form-error">
              {errorMessage}
            </p>
          )}
          {status === 'success' && (
            <p role="status" className="form-success">
              Richiesta inviata con successo. Ti ricontatteremo al più presto.
            </p>
          )}

          <button className="btn primary" type="submit" disabled={status === 'loading'}>
            {status === 'loading' ? 'Invio in corso...' : 'Invia richiesta'}
          </button>
        </form>
      </div>
    </section>
  );
}