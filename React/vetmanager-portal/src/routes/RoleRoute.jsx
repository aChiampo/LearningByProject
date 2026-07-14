import { useContext } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { AppContext } from '../context/AppContext';
import { ROLE_CONFIG } from '../data/roleConfig';

export default function RoleRoute({ role }) {
  const { currentRole, isAuthLoading } = useContext(AppContext);

  if (isAuthLoading) {
    return null;
  }

  if (currentRole !== role) {
    return <Navigate to={ROLE_CONFIG[currentRole]?.dashboard ?? '/login'} replace />;
  }

  return <Outlet />;
}
