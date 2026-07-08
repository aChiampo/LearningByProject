import { Navigate, Outlet } from 'react-router-dom';
import { useContext } from 'react';
import { AppContext } from '../context/AppContext';

export default function ProtectedRoute() {
  const { isLogged } = useContext(AppContext);

  if (!isLogged) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}
