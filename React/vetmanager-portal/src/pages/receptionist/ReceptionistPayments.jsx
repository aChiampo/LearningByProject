import { useContext } from 'react';
import { AppContext } from '../../context/AppContext';
import { ROLE_CONFIG } from '../../data/roleConfig';
import PageTitle from '../../components/common/PageTitle';
import EmptyMessage from '../../components/common/EmptyMessage';

export default function ReceptionistPayments() {
  const { currentRole } = useContext(AppContext);
  const config = ROLE_CONFIG[currentRole];

  return (
    <div>
      <PageTitle eyebrow="Cassa" title="Gestione Pagamenti" />

      <div className="panel section-spaced-sm">
        <h2>Pendenze Attive</h2>
        {config?.pendingPayments?.length > 0 ? (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Intestatario</th>
                  <th>Prestazione</th>
                  <th>Importo</th>
                  <th>Azioni</th>
                </tr>
              </thead>
              <tbody>
                {config.pendingPayments.map((payment, index) => (
                  <tr key={index}>
                    <td>{payment.clienteNome}</td>
                    <td>{payment.descrizioneVisita}</td>
                    <td><strong>Euro {payment.cifra}</strong></td>
                    <td>
                      <div className="actions-row">
                        <button className="btn btn-primary btn-sm" onClick={() => window.alert('Pagamento registrato')}>Registra Saldo</button>
                        <button className="btn btn-outline btn-sm" onClick={() => window.alert('Sollecito inviato')}>Invia Sollecito</button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <EmptyMessage>Ottimo lavoro! Non ci sono pagamenti in sospeso da riscuotere.</EmptyMessage>
        )}
      </div>
    </div>
  );
}
