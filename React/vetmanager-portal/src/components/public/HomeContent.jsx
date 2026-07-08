export default function HomeContent({ clinicHours, services }) {
  return (
    <section className="home-content">
      <section className="home-intro">
        <div>
          <p className="eyebrow">Un punto di riferimento</p>
          <h2>Un solo studio, un percorso di cura continuo.</h2>
        </div>
        <p>
          Dalla prima visita ai controlli periodici, lo studio offre un ambiente accogliente, un
          team stabile e un'organizzazione semplice per gestire le esigenze del tuo animale.
        </p>
      </section>

      <section className="service-list" id="services" aria-label="Servizi principali">
        {services.map((service) => (
          <article className="service-item" key={service.number}>
            <span className="service-number">{service.number}</span>
            <h3>{service.title}</h3>
            <p>{service.description}</p>
          </article>
        ))}
      </section>

      <section className="clinic-details" id="clinic">
        <div className="clinic-message">
          <p className="eyebrow">Vicini quando serve</p>
          <h2>Accoglienza, competenza e continuita clinica.</h2>
          <p>Dott. Camillo Zampetti - Bergamo Alta, IT</p>
          <a className="btn primary" href="#booking-options">
            Richiedi un appuntamento
          </a>
        </div>
        <div className="clinic-hours">
          <h3>Orari</h3>
          {clinicHours.map((row) => (
            <div className="info-row" key={row.label}>
              <span>{row.label}</span>
              <strong>{row.value}</strong>
            </div>
          ))}
        </div>
      </section>
    </section>
  );
}
