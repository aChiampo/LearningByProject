export default function RegistrationForm({
  backLabel = 'Torna alle opzioni',
  form,
  isSubmitting,
  onBack,
  onChange,
  onLogin,
  onSubmit,
  status,
}) {
  return (
    <section className="login-screen public-form-screen home-public-section" id="registrati">
      <div className="login-shell public-form-shell">
        <div className="login-copy">
          <button className="login-back login-back-button" type="button" onClick={onBack}>
            {backLabel}
          </button>
          <p className="eyebrow">Nuovo profilo paziente</p>
          <h1>Inizia da qui.</h1>
          <p>
            Crea il tuo profilo per gestire appuntamenti, documenti clinici e pagamenti in un unico
            spazio.
          </p>
        </div>

        <form className="login-form public-form" aria-label="Modulo di registrazione" onSubmit={onSubmit}>
          <div>
            <p className="eyebrow">Registrazione</p>
            <h2>Crea il tuo profilo</h2>
          </div>
          <div className="form-grid-two">
            <label htmlFor="sign-in-name">
              Nome
              <input
                id="sign-in-name"
                name="nome"
                type="text"
                autoComplete="given-name"
                placeholder="Nome"
                value={form.nome}
                onChange={onChange}
                required
              />
            </label>
            <label htmlFor="sign-in-surname">
              Cognome
              <input
                id="sign-in-surname"
                name="cognome"
                type="text"
                autoComplete="family-name"
                placeholder="Cognome"
                value={form.cognome}
                onChange={onChange}
                required
              />
            </label>
          </div>
          <label htmlFor="sign-in-email">
            Email
            <input
              id="sign-in-email"
              name="email"
              type="email"
              autoComplete="email"
              placeholder="nome@esempio.it"
              value={form.email}
              onChange={onChange}
              required
            />
          </label>
          <label htmlFor="sign-in-phone">
            Telefono
            <input
              id="sign-in-phone"
              name="telefono"
              type="tel"
              autoComplete="tel"
              placeholder="Numero di telefono"
              value={form.telefono}
              onChange={onChange}
              required
            />
          </label>
          <label htmlFor="sign-in-password">
            Password
            <input
              id="sign-in-password"
              name="password"
              type="password"
              autoComplete="new-password"
              placeholder="Crea una password"
              value={form.password}
              onChange={onChange}
              required
            />
          </label>
          <button className="btn primary" type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Registrazione...' : 'Registrati'}
          </button>
          {status.message && (
            <p className={`form-status form-status--${status.type}`}>{status.message}</p>
          )}
          <p className="login-note">
            Hai gia un profilo?{' '}
            <button type="button" onClick={onLogin}>
              Accedi
            </button>
          </p>
        </form>
      </div>
    </section>
  );
}
