import { useState } from 'react';
import { AppContext } from './AppContext';

export const AppProvider = ({ children }) => {
  const [currentRole, setCurrentRole] = useState('client');
  const [palette, setPalette] = useState('aurora');
  const [isLogged, setIsLogged] = useState(false);

  return (
    <AppContext.Provider
      value={{
        currentRole,
        setCurrentRole,
        palette,
        setPalette,
        isLogged,
        setIsLogged,
      }}
    >
      {children}
    </AppContext.Provider>
  );
};
