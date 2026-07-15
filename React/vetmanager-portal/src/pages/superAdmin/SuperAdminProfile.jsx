import PageTitle from '../../components/common/PageTitle';
import UserProfile from '../../components/common/UserProfile';

export default function SuperAdminProfile() {
  return (
    <div>
      <PageTitle eyebrow="Configurazione" title="Profilo Amministratore" />
      <UserProfile fallbackName="Direzione San Luca" description="Amministratore" />
    </div>
  );
}
