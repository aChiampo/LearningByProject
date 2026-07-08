import { useContext } from 'react';
import { AppContext } from '../../context/AppContext';
import { ROLE_CONFIG } from '../../data/roleConfig';
import PageTitle from '../../components/common/PageTitle';

export default function ReceptionistProfile() {
  const { currentRole } = useContext(AppContext);
  const config = ROLE_CONFIG[currentRole];

  return (
    <div>
      <PageTitle eyebrow="Personale" title="Profilo Segreteria" />
      <div className="panel panel-narrow">
        <h2>{config?.userName || 'Receptionist'}</h2>
        <p className="muted-text">Responsabile Accoglienza e Amministrazione</p>
        <form className="stack-form" onSubmit={(event) => event.preventDefault()}>
          <label>
            Sede Operativa
            <input type="text" className="form-control" defaultValue="Bergamo Alta - Reception Centrale" disabled />
          </label>
        </form>
      </div>
    </div>
  );
}
