import { Navigate, Route, Routes } from 'react-router-dom';
import PublicLayout from '../layouts/PublicLayout';
import AppLayout from '../layouts/AppLayout';
import ProtectedRoute from './ProtectedRoute';
import RoleRoute from './RoleRoute';
import PlaceholderPage from '../components/common/PlaceholderPage';
import HomePage from '../pages/public/HomePage';
import LoginPage from '../pages/public/LoginPage';
import RegisterPage from '../pages/public/RegisterPage';
import FirstAppointmentPage from '../pages/public/FirstAppointmentPage';
import ClientDashboard from '../pages/client/ClientDashboard';
import ClientProfile from '../pages/client/ClientProfile';
import ClientBooking from '../pages/client/ClientBooking';
import ClientAddAnimal from '../pages/client/ClientAddAnimal';
import ClientBilling from '../pages/client/ClientBilling';
import ClientFaq from '../pages/client/ClientFaq';
import DoctorDashboard from '../pages/doctor/DoctorDashboard';
import DoctorProfile from '../pages/doctor/DoctorProfile';
import DoctorAnimals from '../pages/doctor/DoctorAnimals';
import DoctorFaq from '../pages/doctor/DoctorFaq';
import ManagmentConfiguration from '../pages/doctor/managment/ManagmentConfiguration.jsx'
import ReceptionistDashboard from '../pages/receptionist/ReceptionistDashboard';
import ReceptionistProfile from '../pages/receptionist/ReceptionistProfile';
import ReceptionistAppointments from '../pages/receptionist/ReceptionistAppointments';
import ReceptionistClients from '../pages/receptionist/ReceptionistClients';
import ReceptionistPayments from '../pages/receptionist/ReceptionistPayments';
import ReceptionistFaq from '../pages/receptionist/ReceptionistFaq';
import SuperAdminDashboard from '../pages/superAdmin/SuperAdminDashboard';
import SuperAdminProfile from '../pages/superAdmin/SuperAdminProfile';
import SuperAdminFaq from '../pages/superAdmin/SuperAdminFaq';

export default function AppRouter() {
  return (
    <Routes>
      <Route element={<PublicLayout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/registrati" element={<RegisterPage />} />
        <Route path="/first-appointment" element={<FirstAppointmentPage />} />
      </Route>

      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route element={<RoleRoute role="client" />}>
            <Route path="/client" element={<Navigate to="/client/dashboard" replace />} />
            <Route path="/client/dashboard" element={<ClientDashboard />} />
            <Route path="/client/profile" element={<ClientProfile />} />
            <Route path="/client/booking" element={<ClientBooking />} />
            <Route path="/client/add-animal" element={<ClientAddAnimal />} />
            <Route path="/client/billing" element={<ClientBilling />} />
            <Route path="/client/faq" element={<ClientFaq />} />
          </Route>

          <Route element={<RoleRoute role="doctor" />}>
            <Route path="/doctor" element={<Navigate to="/doctor/dashboard" replace />} />
            <Route path="/doctor/dashboard" element={<DoctorDashboard />} />
            <Route path="/doctor/profile" element={<DoctorProfile />} />
            <Route path="/doctor/animals" element={<DoctorAnimals />} />
            <Route path="/doctor/faq" element={<DoctorFaq />} />
            <Route path="/doctor/management" element={<ManagmentConfiguration />} />
          </Route>

          <Route element={<RoleRoute role="receptionist" />}>
            <Route path="/receptionist" element={<Navigate to="/receptionist/dashboard" replace />} />
            <Route path="/receptionist/dashboard" element={<ReceptionistDashboard />} />
            <Route path="/receptionist/profile" element={<ReceptionistProfile />} />
            <Route path="/receptionist/appointments" element={<ReceptionistAppointments />} />
            <Route path="/receptionist/clients" element={<ReceptionistClients />} />
            <Route path="/receptionist/payments" element={<ReceptionistPayments />} />
            <Route path="/receptionist/faq" element={<ReceptionistFaq />} />
          </Route>

          <Route element={<RoleRoute role="super-admin" />}>
            <Route path="/super-admin" element={<Navigate to="/super-admin/dashboard" replace />} />
            <Route path="/super-admin/dashboard" element={<SuperAdminDashboard />} />
            <Route path="/super-admin/profile" element={<SuperAdminProfile />} />
            <Route path="/super-admin/staff" element={<PlaceholderPage />} />
            <Route path="/super-admin/settings" element={<PlaceholderPage />} />
            <Route path="/super-admin/faq" element={<SuperAdminFaq />} />
          </Route>
        </Route>
      </Route>

      <Route path="*" element={<PlaceholderPage />} />
    </Routes>
  );
}
