import { useNavigate } from 'react-router-dom';
import PageTitle from '../../components/common/PageTitle';
import ClientAnimalForm from '../../components/animals/ClientAnimalForm';

export default function ClientAddAnimal() {
  const navigate = useNavigate();

  return (
    <div>
      <PageTitle eyebrow="Area Riservata" title="Aggiungi animale" />
      <ClientAnimalForm onCreated={() => navigate('/client/dashboard')} />
    </div>
  );
}
