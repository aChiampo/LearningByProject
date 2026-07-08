import { useContext } from 'react';
import { AppContext } from '../../context/AppContext';
import { ROLE_CONFIG } from '../../data/roleConfig';
import PageTitle from '../../components/common/PageTitle';

export default function ClientProfile() {
  const { currentRole } = useContext(AppContext);
  const config = ROLE_CONFIG[currentRole];

  return (
    <div>
      <PageTitle eyebrow="Area Riservata" title="Profilo Personale" />

      <div className="panel panel-narrow">
        <form className="stack-form" onSubmit={(event) => event.preventDefault()}>
          <label>
            Nome Completo
            <input type="text" className="form-control" defaultValue={config?.userName || 'Andrea Rossi'} />
          </label>
          <label>
            Email
            <input type="email" className="form-control" defaultValue="andrea.rossi@example.com" />
          </label>
          <label>
            Telefono
            <input type="tel" className="form-control" defaultValue="+39 333 1234567" />
          </label>
          <label>
            Ruolo di Accesso
            <input type="text" className="form-control" value={config?.label || ''} disabled readOnly />
          </label>
          <button type="submit" className="btn btn-primary align-start">
            Salva Modifiche
          </button>
        </form>
      </div>
    </div>
  );
}
