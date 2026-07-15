import PageTitle from '../../components/common/PageTitle';
import UserProfile from '../../components/common/UserProfile';

export default function ReceptionistProfile() {
  return (
    <div>
      <PageTitle eyebrow="Personale" title="Profilo Segreteria" />
      <UserProfile fallbackName="Receptionist" description="Responsabile Accoglienza e Amministrazione" />
    </div>
  );
}
