
import { AppContext } from '../context/AppContext';
import React, { useState, useEffect, useContext } from 'react';
import { PALETTES, ROLE_CONFIG } from '../data/roleConfig';

export default function Header() {
  // Recuperiamo isLogged e setIsLogged dallo stato globale
  const { currentRole, currentScreen, setCurrentScreen, isLogged, setIsLogged, palette, setPalette } = useContext(AppContext);
  
  const userConfig = ROLE_CONFIG[currentRole];
// Applica la palette selezionata al tag globale HTML ogni volta che cambia
useEffect(() => {
    console.log("Applico al body il tema:", `theme-${palette}`);
    
    if (palette) {
      // 1. Rimuove dal body i vecchi temi per evitare che si sovrappongano
      document.body.classList.remove(
        'theme-aurora', 
        'theme-lago', 
        'theme-energia', 
        'theme-iris', 
        'theme-cielo', 
        'theme-rosa'
      );
      
      // 2. Aggiunge la classe esatta (es. "theme-lago") al tag <body>
      document.body.classList.add(`theme-${palette}`);
    }
  }, [palette]);
  return (
    <header className="topbar" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0 24px' }}>
      
      {/* Brand principale */}
      <a 
        className="brand" 
        href="#homepage" 
        onClick={(e) => {
          e.preventDefault();
          setCurrentScreen('homepage');
        }} 
        aria-label="Vai alla homepage"
      >
        <span className="brand-mark">S</span>
        <span>
          <strong>Studio Veterinario San Luca</strong>
          <small>Cura, visite e prevenzione</small>
        </span>
      </a>

      {/* BLOCCO DI ACCESSO / PROFILO INTELLIGENTE */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
        
        {/* Caso 1: L'utente NON è loggato */}
        {!isLogged && (
          <button 
            onClick={() => setCurrentScreen('router')}
            style={{ 
              background: 'var(--primary)', 
              color: '#ffffff', 
              border: 'none', 
              padding: '10px 20px', 
              borderRadius: '20px', 
              fontWeight: 'bold', 
              cursor: 'pointer',
              fontSize: '0.95rem'
            }}
          >
            Accedi al Portale
          </button>
        )}

        {/* Caso 2: L'utente HA FATTO L'ACCESSO ed è tornato sulla Homepage */}
        {isLogged && (currentScreen === 'homepage' || currentScreen === 'router') && (
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <span style={{ fontSize: '0.9rem', color: 'var(--muted)' }}>
              Ciao, <strong>{userConfig?.userName}</strong>
            </span>
            <button 
              onClick={() => setCurrentScreen(`dashboard-${currentRole}`)}
              style={{ 
                background: 'var(--primary)', 
                color: '#ffffff', 
                border: 'none', 
                padding: '8px 16px', 
                borderRadius: '20px', 
                cursor: 'pointer',
                fontWeight: 'bold'
              }}
            >
              Torna alla Dashboard
            </button>
          </div>
        )}

        {/* Caso 3: L'utente è dentro un'area privata (Dashboard, FAQ, ecc.) */}
        {isLogged && currentScreen !== 'homepage' && currentScreen !== 'router' && (
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
            <span style={{ fontSize: '0.85rem', color: 'var(--muted)', borderRight: '1px solid var(--line)', paddingRight: '16px' }}>
              Utenza: <strong>{userConfig?.userName}</strong>
            </span>
            <button 
              onClick={() => {
                if (window.confirm('Vuoi uscire dal tuo account?')) {
                  setIsLogged(false); // Slogga l'utente nello stato globale
                  setCurrentScreen('router');
                }
              }}
              style={{ 
                background: 'transparent', 
                color: 'var(--danger)', 
                border: '1px solid var(--danger)', 
                padding: '4px 10px', 
                borderRadius: '4px', 
                cursor: 'pointer',
                fontSize: '0.8rem',
                opacity: 0.7,
                transition: 'opacity 0.2s'
              }}
              onMouseOver={(e) => e.currentTarget.style.opacity = 1}
              onMouseOut={(e) => e.currentTarget.style.opacity = 0.7}
            >
              ➔ Disconnetti
            </button>
          </div>
        )}
      </div>

      {/* Selettore Palette Colori */}
      <div className="theme-switches">
        <fieldset className="palette-picker">
          <legend>Palette</legend>
          {PALETTES.map((p) => (
            <label key={p} className={`palette-option ${p}`}>
              <input 
                type="radio" 
                name="palette" 
                checked={palette === p} 
                onChange={() => setPalette(p)} 
              />
              <span></span>{p.charAt(0).toUpperCase() + p.slice(1)}
            </label>
          ))}
        </fieldset>
      </div>
    </header>
  );
}