import React, { useContext } from 'react';
import { AppContext } from '../context/AppContext';
import { ROLE_CONFIG } from '../data/roleConfig.js';

export default function Sidebar() {
  const { currentRole, currentScreen, setCurrentScreen } = useContext(AppContext);
  
  const config = ROLE_CONFIG[currentRole];

  if (!config) return null;

  return (
    <nav className="navbar" aria-label={`Navigazione ${config.label}`}>
      
      {/* Blocco Utente */}
      <div className="nav-user">
        <span>{config.label}</span>
        <strong>{config.userName}</strong>
      </div>

      {/* Lista dei Link */}
      <div>
        {config.navigation.map((item, index) => {
          const isActive = currentScreen === item.target;
          
          return (
            <a
              key={index}
              href={`#${item.target}`}
              className={isActive ? 'active' : ''}
              onClick={(e) => {
                e.preventDefault();
                setCurrentScreen(item.target);
              }}
            >
              {item.label}
            </a>
          );
        })}
      </div>
    </nav>
  );
}