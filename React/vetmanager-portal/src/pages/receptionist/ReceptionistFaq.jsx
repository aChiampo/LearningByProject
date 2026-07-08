import PageTitle from '../../components/common/PageTitle';

export default function ReceptionistFaq() {
  return (
    <div>
      <PageTitle eyebrow="Supporto Segreteria" title="FAQ Reception" />
      <section className="panel faq-list section-spaced-sm">
        <details>
          <summary>Come si registra un pagamento?</summary>
          <p>Vai su "Gestisci pagamenti", individua la riga del cliente desiderata e seleziona "Registra Saldo" per incassare la fattura.</p>
        </details>
        <details>
          <summary>Cosa fare per modificare o cancellare un appuntamento?</summary>
          <p>Nel menu "Gestisci appuntamenti" puoi usare i bottoni "Modifica" o "Cancella" a fianco di ogni record del planning.</p>
        </details>
      </section>
    </div>
  );
}
