import React, { createContext, useState } from 'react';

export const AppContext = createContext();

export const AppProvider = ({ children }) => {
  const [currentRole, setCurrentRole] = useState('client');
  const [currentScreen, setCurrentScreen] = useState('homepage');
  const [palette, setPalette] = useState('aurora');
  // NUOVO: Stato per ricordarsi se l'utente è loggato o meno
  const [isLogged, setIsLogged] = useState(false); 

  return (
    <AppContext.Provider value={{ 
      currentRole, setCurrentRole, 
      currentScreen, setCurrentScreen, 
      palette, setPalette,
      isLogged, setIsLogged // Esponiamo le nuove variabili
    }}>
      {children}
    </AppContext.Provider>
  );
};