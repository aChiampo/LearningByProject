import { useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppContext } from '../../context/AppContext';
import { ROLE_CONFIG } from '../../data/roleConfig';
import PageTitle from '../../components/common/PageTitle';
import EmptyMessage from '../../components/common/EmptyMessage';

export default function ClientDashboard() {
  const navigate = useNavigate();
  const { currentRole } = useContext(AppContext);
  const config = ROLE_CONFIG[currentRole];

  return (
    <div>
      <PageTitle eyebrow="Area Riservata" title="Dashboard Cliente" />

      <section className="section-block">
        <h2>I tuoi animali</h2>

        {config?.animals?.length > 0 ? (
          <div className="grid-cards">
            {config.animals.map((animale, index) => (
              <article key={index} className="panel card">
                <div className="card-header">
                  <h3>{animale.nome}</h3>
                  <span className="badge">{animale.specie}</span>
                </div>
                <p className="muted-text">Razza: {animale.razza} - Eta: {animale.eta}</p>
                <div className="actions-row">
                  <button className="btn btn-primary btn-sm" onClick={() => navigate('/client/booking')}>
                    Prenota Visita
                  </button>
                  <button className="btn btn-outline btn-sm">Apri Cartella</button>
                </div>
              </article>
            ))}
          </div>
        ) : (
          <EmptyMessage>Nessun animale registrato nel tuo profilo.</EmptyMessage>
        )}
      </section>

      <section className="section-block section-spaced">
        <h2>Prossimi Appuntamenti</h2>

        {config?.appointments?.length > 0 ? (
          <div className="panel table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Animale</th>
                  <th>Data e Ora</th>
                  <th>Prestazione</th>
                  <th>Veterinario</th>
                  <th>Stato</th>
                </tr>
              </thead>
              <tbody>
                {config.appointments.map((appointment, index) => (
                  <tr key={index}>
                    <td><strong>{appointment.animalName}</strong></td>
                    <td>{appointment.date}</td>
                    <td>{appointment.type}</td>
                    <td>{appointment.doctor}</td>
                    <td><span className="badge badge-success">{appointment.status}</span></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <EmptyMessage>Non ci sono appuntamenti in programma.</EmptyMessage>
        )}
      </section>
    </div>
  );
}
