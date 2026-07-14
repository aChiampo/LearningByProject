import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import RegistrationForm from '../../components/public/RegistrationForm';
import { INITIAL_REGISTRATION_FORM } from '../../data/homePageData';
import { registerClient } from '../../services/authApi';

export default function RegisterPage() {
  const navigate = useNavigate();
  const [registrationForm, setRegistrationForm] = useState(INITIAL_REGISTRATION_FORM);
  const [registrationStatus, setRegistrationStatus] = useState({ type: '', message: '' });
  const [isRegistering, setIsRegistering] = useState(false);

  function handleRegistrationChange(event) {
    const { name, value } = event.target;
    setRegistrationForm((currentForm) => ({
      ...currentForm,
      [name]: value,
    }));
  }

  async function handleRegistrationSubmit(event) {
    event.preventDefault();
    setIsRegistering(true);
    setRegistrationStatus({ type: '', message: '' });

    try {
      await registerClient(registrationForm);
      setRegistrationForm(INITIAL_REGISTRATION_FORM);
      setRegistrationStatus({
        type: 'success',
        message: 'Registrazione completata. Ora puoi accedere al portale.',
      });
    } catch (error) {
      console.error('Error:', error);
      setRegistrationStatus({
        type: 'error',
        message: error.message || 'Non e stato possibile completare la registrazione.',
      });
    } finally {
      setIsRegistering(false);
    }
  }

  return (
    <RegistrationForm
      form={registrationForm}
      isSubmitting={isRegistering}
      onBack={() => navigate('/#booking-options')}
      onChange={handleRegistrationChange}
      onLogin={() => navigate('/login')}
      onSubmit={handleRegistrationSubmit}
      status={registrationStatus}
    />
  );
}
