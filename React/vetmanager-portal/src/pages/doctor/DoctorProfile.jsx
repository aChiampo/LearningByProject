import { useContext } from 'react';
import { AppContext } from '../../context/AppContext';
import { ROLE_CONFIG } from '../../data/roleConfig';
import PageTitle from '../../components/common/PageTitle';

export default function DoctorProfile() {
  const { currentRole } = useContext(AppContext);
  const config = ROLE_CONFIG[currentRole];

  return (
    <div>
      <PageTitle eyebrow="Impostazioni Mediche" title="Profilo Medico" />

      <div className="panel panel-narrow">
        <div className="profile-avatar-row">
          <div className="profile-avatar">CZ</div>
          <div>
            <h2>{config?.userName || 'Dottore'}</h2>
            <p className="muted-text">Chirurgo ed Esperto Piccoli Animali</p>
          </div>
        </div>

        <form className="stack-form" onSubmit={(event) => event.preventDefault()}>
          <label>
            Numero Iscrizione Ordine
            <input type="text" className="form-control" defaultValue="N. 12345 - Bergamo" disabled />
          </label>
          <label>
            Specializzazione Primaria
            <input type="text" className="form-control" defaultValue="Medicina e Chirurgia degli animali d'affezione" />
          </label>
          <button type="submit" className="btn btn-primary align-start">Aggiorna Profilo</button>
        </form>
      </div>
    </div>
  );
}
