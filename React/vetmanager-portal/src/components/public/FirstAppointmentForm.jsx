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

        <form className="login-form public-form" aria-label={ariaLabel}>
          <div>
            <p className="eyebrow">{formEyebrow}</p>
            <h2>{formTitle}</h2>
          </div>
          <label htmlFor="first-owner">
            Nome e cognome
            <input id="first-owner" name="owner" type="text" autoComplete="name" placeholder="Il tuo nome" />
          </label>
          <div className="form-grid-two">
            <label htmlFor="first-email">
              Email
              <input id="first-email" name="email" type="email" autoComplete="email" placeholder="nome@esempio.it" />
            </label>
            <label htmlFor="first-phone">
              Telefono
              <input id="first-phone" name="phone" type="tel" autoComplete="tel" placeholder="Numero di telefono" />
            </label>
          </div>
          <div className="form-grid-two">
            <label htmlFor="first-animal">
              Nome dell'animale
              <input id="first-animal" name="animal" type="text" placeholder="Nome" />
            </label>
            <label htmlFor="first-species">
              Animale
              <select id="first-species" name="species" defaultValue="Cane">
                <option>Cane</option>
                <option>Gatto</option>
                <option>Altro piccolo animale</option>
              </select>
            </label>
          </div>
          <label htmlFor="first-reason">
            Motivo della visita
            <select id="first-reason" name="reason" defaultValue="Controllo generale">
              <option>Controllo generale</option>
              <option>Vaccinazione</option>
              <option>Consulenza</option>
              <option>Altro</option>
            </select>
          </label>
          <label htmlFor="first-notes">
            Informazioni utili
            <textarea id="first-notes" name="notes" rows="4" placeholder="Aggiungi una breve nota, se necessario"></textarea>
          </label>
          <button className="btn primary" type="button">
            Invia richiesta
          </button>
        </form>
      </div>
    </section>
  );
}
