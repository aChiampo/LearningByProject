import { useNavigate } from 'react-router-dom';
import FirstAppointmentForm from '../../components/public/FirstAppointmentForm';

const addAnimalCopy = {
  eyebrow: 'Aggiungi animale',
  title: 'Raccontaci chi dobbiamo seguire.',
  description:
    'Invia le informazioni essenziali sul tuo animale. Lo studio potra ricontattarti per completare la registrazione e organizzare il primo appuntamento.',
  formEyebrow: 'Richiesta nuovo animale',
  formTitle: 'Dati principali',
  ariaLabel: 'Richiesta aggiunta nuovo animale',
};

export default function ClientAddAnimal() {
  const navigate = useNavigate();

  return (
    <FirstAppointmentForm
      backLabel="Torna alla dashboard"
      copy={addAnimalCopy}
      onBack={() => navigate('/client/dashboard')}
    />
  );
}
