export default function HomeHero({ currentRole, dashboardPath, isLogged, onNavigate }) {
  return (
    <section className="home-hero">
      <div className="hero-copy">
        <p className="eyebrow">Cura veterinaria a Bergamo Alta</p>
        <h1>Piu attenzione per loro, piu serenita per te.</h1>
        <p>
          Lo Studio Veterinario San Luca accoglie cani, gatti e piccoli animali con visite attente,
          percorsi di prevenzione e un contatto diretto con il team clinico.
        </p>

        <div className="button-row page-actions">
          {!isLogged && (
            <>
              <a className="btn primary" href="#booking-options">
                Prenota una visita
              </a>
              <button className="btn hero-secondary" onClick={() => onNavigate('/login')}>
                Area pazienti
              </button>
              <button className="btn hero-secondary" onClick={() => onNavigate('/login')}>
                Area riservata
              </button>
            </>
          )}

          {isLogged && currentRole === 'client' && (
            <button className="btn primary" onClick={() => onNavigate('/client/booking')}>
              Prenota una visita
            </button>
          )}

          {isLogged && currentRole === 'receptionist' && (
            <button className="btn primary" onClick={() => onNavigate('/receptionist/dashboard')}>
              Gestisci appuntamenti
            </button>
          )}

          {isLogged && currentRole === 'doctor' && (
            <button className="btn primary" onClick={() => onNavigate('/doctor/dashboard')}>
              Visualizza agenda
            </button>
          )}

          {isLogged && currentRole === 'super-admin' && (
            <button className="btn primary" onClick={() => onNavigate(dashboardPath)}>
              Pannello Admin
            </button>
          )}
        </div>
      </div>
    </section>
  );
}
