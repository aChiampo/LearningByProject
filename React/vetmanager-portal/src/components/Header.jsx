import { useContext, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { AppContext } from '../context/AppContext';
import { ROLE_CONFIG } from '../data/roleConfig';

export default function Header() {
  const navigate = useNavigate();
  const location = useLocation();
  const { currentRole, currentUser, isLogged, logout } = useContext(AppContext);

  const userConfig = ROLE_CONFIG[currentRole];
  const userName = [currentUser?.nome, currentUser?.cognome].filter(Boolean).join(' ') || userConfig?.userName;
  const publicRoutes = ['/', '/login', '/registrati', '/first-appointment'];
  const isPublicRoute = publicRoutes.includes(location.pathname);

  useEffect(() => {
    document.body.classList.remove(
      'theme-aurora',
      'theme-lago',
      'theme-energia',
      'theme-iris',
      'theme-cielo',
      'theme-rosa',
    );

    document.body.classList.add('theme-cielo');
  }, []);

  function handleLogout() {
    if (window.confirm('Vuoi uscire dal tuo account?')) {
      logout();
      navigate('/login');
    }
  }

  return (
    <header className="topbar">
      <Link className="brand" to="/" aria-label="Vai alla homepage">
        <span className="brand-mark">S</span>
        <span>
          <strong>Studio Veterinario San Luca</strong>
          <small>Cura, visite e prevenzione</small>
        </span>
      </Link>

      <div className="topbar-actions">
        {!isLogged && (
          <button className="portal-button" onClick={() => navigate('/login')}>
            Accedi al Portale
          </button>
        )}

        {isLogged && isPublicRoute && (
          <div className="user-actions">
            <span className="user-chip">
              Ciao, <strong>{userName}</strong>
            </span>
            <button className="portal-button" onClick={() => navigate(userConfig?.dashboard ?? '/')}>
              Torna alla Dashboard
            </button>
          </div>
        )}

        {isLogged && !isPublicRoute && (
          <div className="user-actions">
            <span className="user-chip user-chip--bordered">
              Utenza: <strong>{userName}</strong>
            </span>
            <button className="logout-button" onClick={handleLogout}>
              Disconnetti
            </button>
          </div>
        )}
      </div>

    </header>
  );
}
