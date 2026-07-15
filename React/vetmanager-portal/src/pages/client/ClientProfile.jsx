import PageTitle from '../../components/common/PageTitle';
import UserProfile from '../../components/common/UserProfile';

export default function ClientProfile() {
  return (
    <div>
      <PageTitle eyebrow="Area Riservata" title="Profilo Personale" />
      <UserProfile fallbackName="Andrea Rossi" description="Area Cliente" />
    </div>
  );
}
