import { useContext, useEffect } from 'react';
import { Outlet } from 'react-router-dom';
import { AppContext } from '../context/AppContext';

export default function RoleRoute({ role }) {
  const { currentRole, setCurrentRole } = useContext(AppContext);

  useEffect(() => {
    if (currentRole !== role) {
      setCurrentRole(role);
    }
  }, [currentRole, role, setCurrentRole]);

  return <Outlet />;
}
