export default function BookingOptions({ options, onLogin, onNavigate }) {
  return (
    <section className="route-screen home-route-section" id="booking-options">
      <div className="route-shell">
        <div className="route-heading">
          <p className="eyebrow">Prenota una visita</p>
          <h1>Come vuoi iniziare?</h1>
          <p>
            Scegli il percorso piu adatto per te o per il tuo animale. Potrai sempre tornare qui e
            cambiare opzione.
          </p>
        </div>
        <div className="route-options">
          {options.map((option) => {
            const content = (
              <>
                <span>{option.eyebrow}</span>
                <strong>{option.title}</strong>
                <small>{option.description}</small>
              </>
            );

            if (option.type === 'login') {
              return (
                <button className="route-option" key={option.type} type="button" onClick={onLogin}>
                  {content}
                </button>
              );
            }

            return (
              <button
                className="route-option"
                key={option.type}
                type="button"
                onClick={() => onNavigate(option.path)}
              >
                {content}
              </button>
            );
          })}
        </div>
      </div>
    </section>
  );
}
