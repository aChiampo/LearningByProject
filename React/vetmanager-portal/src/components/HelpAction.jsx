import { useContext } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { AppContext } from '../context/AppContext';
import { ROLE_CONFIG } from '../data/roleConfig.js';

export default function HelpAction() {
  const navigate = useNavigate();
  const location = useLocation();
  const { currentRole } = useContext(AppContext);
  const config = ROLE_CONFIG[currentRole];
  const isPublicRoute = location.pathname === '/' || location.pathname === '/login';

  if (!config || isPublicRoute || location.pathname === config.faq) {
    return null;
  }

  return (
    <button
      className="help-action is-visible"
      title="Aiuto contestuale"
      aria-label="Apri FAQ del ruolo"
      onClick={() => navigate(config.faq)}
    >
      ?
    </button>
  );
}
