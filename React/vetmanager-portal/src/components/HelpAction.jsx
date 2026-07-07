import React, { useContext } from 'react';
import { AppContext } from '../context/AppContext';
import { ROLE_CONFIG } from '../data/roleConfig.js';

export default function HelpAction() {
  const { currentRole, currentScreen, setCurrentScreen } = useContext(AppContext);

  const config = ROLE_CONFIG[currentRole];

  // Se il ruolo non esiste o siamo in una schermata pubblica (es. homepage), non mostrare il punto di domanda
  if (!config || currentScreen === 'homepage' || currentScreen === 'router') {
    return null;
  }

  // Se l'utente si trova già sulla pagina FAQ del suo ruolo, nascondi il pulsante
  if (currentScreen === config.faq) {
    return null;
  }

  return (
    <button
      className="help-action is-visible"
      title="Aiuto contestuale"
      aria-label="Apri FAQ del ruolo"
      onClick={() => setCurrentScreen(config.faq)}
    >
      ?
    </button>
  );
}