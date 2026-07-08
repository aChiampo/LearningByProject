import { useNavigate } from 'react-router-dom';
import FirstAppointmentForm from '../../components/public/FirstAppointmentForm';

export default function FirstAppointmentPage() {
  const navigate = useNavigate();

  return <FirstAppointmentForm onBack={() => navigate('/#booking-options')} />;
}
