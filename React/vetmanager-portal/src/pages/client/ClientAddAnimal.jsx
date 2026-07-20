import { useNavigate } from 'react-router-dom';
import PageTitle from '../../components/common/PageTitle';
import AddAnimalForm from '../../components/animals/AddAnimalForm';

export default function ClientAddAnimal() {
  const navigate = useNavigate();

  return (
    <div>
      <PageTitle eyebrow="Area Riservata" title="Aggiungi animale" />
      <AddAnimalForm onCreated={() => navigate('/client/dashboard')} />
    </div>
  );
}
