import PageTitle from '../../components/common/PageTitle';
import UserProfile from '../../components/common/UserProfile';

export default function DoctorProfile() {
  return (
    <div>
      <PageTitle eyebrow="Impostazioni Mediche" title="Profilo Medico" />
      <UserProfile fallbackName="Dottore" description="Area Clinica" />
    </div>
  );
}
