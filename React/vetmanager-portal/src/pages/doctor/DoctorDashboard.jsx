import { useContext, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppContext } from '../../context/AppContext';
import { ROLE_CONFIG } from '../../data/roleConfig';
import PageTitle from '../../components/common/PageTitle';
import EmptyMessage from '../../components/common/EmptyMessage';

export default function DoctorDashboard() {
  const navigate = useNavigate();
  const { currentRole, currentUser } = useContext(AppContext);
  const config = ROLE_CONFIG[currentRole];

  const [appointments, setAppointments] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  // ID del veterinario loggato (preso da contesto o di default per test)
  const doctorId = currentUser?.id || 1; 

  useEffect(() => {
    let isMounted = true;

    async function loadTodayAppointments() {
      setIsLoading(true);
      setError('');

      try {
        // Data di oggi in formato YYYY-MM-DD
        const todayStr = new Date().toISOString().split('T')[0];

        // Usiamo l'endpoint ESISTENTE nel tuo VisitaController: /api/visite/params
        const response = await fetch('http://localhost:9020/api/visite/params', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          // Inviamo il VisitParamDTO con ID del dottore e la data di oggi
          body: JSON.stringify({
            doctorID: doctorId,
            date: todayStr
          }),
        });
        
        if (!response.ok) {
          throw new Error('Impossibile caricare l\'agenda dal server');
        }

        const data = await response.json(); // Restituisce una lista di VisitaDto

        if (isMounted) {
          // Mappiamo i VisitaDto ricevuti per la tabella React
          const formattedData = data.map((visita, idx) => ({
            id: visita.id || idx,
            orario: visita.fasciaOraria || 'Mattina',
            pazienteId: visita.animale?.id,
            pazienteNome: visita.animale?.nome || 'Paziente',
            pazienteRazza: visita.animale?.razza || '',
            proprietario: visita.animale?.proprietario 
              ? `${visita.animale.proprietario.nome} ${visita.animale.proprietario.cognome}`
              : 'Proprietario',
            motivo: visita.tipoVisita?.nome || visita.note || 'Visita di controllo',
            stato: visita.stato || 'IN_ATTESA',
          }));

          setAppointments(formattedData);
        }
      } catch (err) {
        if (isMounted) {
          console.warn('Backend non raggiungibile, utilizzo dati mock:', err);
          // Fallback automatico se il DB/Backend è spento
          setAppointments(config?.todayAppointments || []);
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    loadTodayAppointments();

    return () => {
      isMounted = false;
    };
  }, [doctorId, config]);

  const getBadgeClass = (stato) => {
    switch (stato) {
      case 'COMPLETATA':
        return 'badge-success';
      case 'IN_CORSO':
        return 'badge-primary';
      case 'ANNULLATA':
        return 'badge-danger';
      default:
        return 'badge-neutral';
    }
  };

  return (
    <div>
      <PageTitle eyebrow="Area Professionale" title="Dashboard Clinica" />

      <div className="panel section-spaced-sm">
        <h2>Agenda Visite di Oggi</h2>
        <p className="muted-text">Controllo appuntamenti attivi filtrati per le tue visite</p>

        {isLoading && <p className="muted-text">Caricamento appuntamenti...</p>}
        {error && <p className="form-status form-status--error">{error}</p>}

        {!isLoading && appointments.length > 0 ? (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Orario</th>
                  <th>Paziente</th>
                  <th>Proprietario</th>
                  <th>Motivo Visita</th>
                  <th>Stato</th>
                  <th>Azioni</th>
                </tr>
              </thead>
              <tbody>
                {appointments.map((visit, index) => (
                  <tr key={visit.id || index}>
                    <td>
                      <strong>{visit.orario}</strong>
                    </td>
                    <td>
                      {visit.pazienteNome}{' '}
                      {visit.pazienteRazza && (
                        <span className="muted-text">({visit.pazienteRazza})</span>
                      )}
                    </td>
                    <td>{visit.proprietario}</td>
                    <td>{visit.motivo}</td>
                    <td>
                      <span className={`badge ${getBadgeClass(visit.stato)}`}>
                        {visit.stato}
                      </span>
                    </td>
                    <td>
                      <div style={{ display: 'flex', gap: '6px' }}>
                        {/* Scheda Clinica Animale */}
                        <button
                          className="btn btn-outline btn-sm"
                          title="Scheda Clinica Animale"
                          onClick={() =>
                            navigate(`/doctor/animals?animaleId=${visit.pazienteId || ''}`)
                          }
                        >
                          🐾 Cartella
                        </button>

                        {/* Scheda Visita */}
                        <button
                          className="btn btn-primary btn-sm"
                          title="Scheda Visita"
                          onClick={() =>
                            navigate(`/doctor/management?visitaId=${visit.id || ''}`)
                          }
                        >
                          Visita
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          !isLoading && (
            <EmptyMessage>
              Nessun appuntamento programmato per la giornata di oggi.
            </EmptyMessage>
          )
        )}
      </div>
    </div>
  );
}