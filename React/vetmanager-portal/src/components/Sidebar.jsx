import { useContext } from 'react';
import { NavLink } from 'react-router-dom';
import { AppContext } from '../context/AppContext';
import { ROLE_CONFIG } from '../data/roleConfig.js';

export default function Sidebar() {
  const { currentRole, currentUser } = useContext(AppContext);
  const config = ROLE_CONFIG[currentRole];
  const userName = [currentUser?.nome, currentUser?.cognome].filter(Boolean).join(' ') || config?.userName;

  if (!config) return null;

  return (
    <nav className="navbar" aria-label={`Navigazione ${config.label}`}>
      <div className="nav-user">
        <span>{config.label}</span>
        <strong>{userName}</strong>
      </div>

      <div>
        {config.navigation.map((item) => (
          <NavLink
            key={item.target}
            to={item.target}
            className={({ isActive }) => (isActive ? 'active' : '')}
          >
            {item.label}
          </NavLink>
        ))}
      </div>
    </nav>
  );
}
