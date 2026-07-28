import { useEffect, useState } from 'react';
import { AppContext } from './AppContext';
import {
  clearAuthSession,
  readStoredAuthSession,
  storeAuthSession,
} from '../services/apiClient';
import { fetchCurrentUser, loginUser } from '../services/authApi';

export const AppProvider = ({ children }) => {
  const [authSession, setAuthSession] = useState(() => readStoredAuthSession());
  const [currentUser, setCurrentUser] = useState(() => authSession?.user ?? null);
  const [currentRole, setCurrentRole] = useState(() => authSession?.user?.role ?? 'client');
  const [isAuthLoading, setIsAuthLoading] = useState(Boolean(authSession?.token));
  const isLogged = Boolean(authSession?.token && currentUser);

  useEffect(() => {
    let isMounted = true;

    async function restoreSession() {
      const storedSession = readStoredAuthSession();

      if (!storedSession?.token) {
        setIsAuthLoading(false);
        return;
      }

      try {
        const restoredUser = await fetchCurrentUser();

        if (!isMounted) {
          return;
        }

        const restoredSession = {
          ...storedSession,
          user: restoredUser,
        };

        storeAuthSession(restoredSession);
        setAuthSession(restoredSession);
        setCurrentUser(restoredUser);
        setCurrentRole(restoredUser.role);
      } catch {
        if (!isMounted) {
          return;
        }

        clearAuthSession();
        setAuthSession(null);
        setCurrentUser(null);
        setCurrentRole('client');
      } finally {
        if (isMounted) {
          setIsAuthLoading(false);
        }
      }
    }

    restoreSession();

    return () => {
      isMounted = false;
    };
  }, []);

  async function login(credentials) {
    const session = await loginUser(credentials);

    setAuthSession(session);
    setCurrentUser(session.user);
    setCurrentRole(session.user.role);

    return session;
  }

  function updateCurrentUser(user) {
    setCurrentUser(user);
    setCurrentRole(user.role);
    setAuthSession((currentSession) => {
      if (!currentSession) {
        return currentSession;
      }

      const updatedSession = {
        ...currentSession,
        user,
      };

      storeAuthSession(updatedSession);
      return updatedSession;
    });
  }

  function logout() {
    clearAuthSession();
    setAuthSession(null);
    setCurrentUser(null);
    setCurrentRole('client');
  }

  return (
    <AppContext.Provider
      value={{
        authSession,
        currentUser,
        currentRole,
        setCurrentRole,
        isLogged,
        isAuthLoading,
        login,
        logout,
        updateCurrentUser,
      }}
    >
      {children}
    </AppContext.Provider>
  );
};
