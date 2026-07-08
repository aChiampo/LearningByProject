import { useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppContext } from '../../context/AppContext';
import { ROLE_CONFIG } from '../../data/roleConfig';

export default function HomePage() {
  const navigate = useNavigate();
  const { currentRole, isLogged } = useContext(AppContext);

  const dashboardPath = ROLE_CONFIG[currentRole]?.dashboard ?? '/login';

  return (
    <>
      <section className="home-hero">
        <div className="hero-copy">
          <h1>Studio Veterinario <br />San Luca</h1>
          <p>Cura, visite e prevenzione per i tuoi amici animali a Bergamo Alta.</p>

          <div className="button-row page-actions">
            {!isLogged && (
              <button className="btn btn-secondary" onClick={() => navigate('/login')}>
                Prenota un servizio
              </button>
            )}

            {isLogged && currentRole === 'client' && (
              <button className="btn btn-secondary" onClick={() => navigate('/client/dashboard')}>
                Prenota un servizio
              </button>
            )}

            {isLogged && currentRole === 'receptionist' && (
              <button className="btn btn-secondary btn-dark" onClick={() => navigate('/receptionist/dashboard')}>
                Gestisci appuntamenti
              </button>
            )}

            {isLogged && currentRole === 'doctor' && (
              <button className="btn btn-secondary" onClick={() => navigate('/doctor/dashboard')}>
                Visualizza registro visite
              </button>
            )}

            {isLogged && currentRole === 'super-admin' && (
              <button className="btn btn-secondary" onClick={() => navigate(dashboardPath)}>
                Pannello Admin
              </button>
            )}
          </div>
        </div>
      </section>

      <div className="home-highlights">
        <div>
          <strong>Bergamo Alta</strong>
          <p>Sede storica facilmente raggiungibile</p>
        </div>
        <div>
          <strong>Dott. Zampetti</strong>
          <p>Specialista in piccoli animali e chirurgia</p>
        </div>
        <div>
          <strong>Pronto Soccorso</strong>
          <p>Reperibilita e supporto continuo</p>
        </div>
      </div>

      <section className="home-content">
        <div className="clinic-message">
          <h2>La nostra filosofia</h2>
          <p>Ci prendiamo cura dei vostri compagni di vita con le migliori tecnologie e una profonda passione, garantendo controlli accurati e terapie personalizzate in un ambiente sereno.</p>
        </div>
      </section>
    </>
  );
}
