import { Navigate, Outlet } from 'react-router-dom';
import { useContext } from 'react';
import { AppContext } from '../context/AppContext';

export default function ProtectedRoute() {
  const { isAuthLoading, isLogged } = useContext(AppContext);

  if (isAuthLoading) {
    return (
      <div className="auth-shell">
        <div className="panel auth-card">Verifica sessione...</div>
      </div>
    );
  }

  if (!isLogged) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}
