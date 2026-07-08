import PageTitle from '../../components/common/PageTitle';

export default function SuperAdminFaq() {
  return (
    <div>
      <PageTitle eyebrow="Manuale Amministratore" title="FAQ Presentazione" />
      <section className="panel faq-list section-spaced-sm">
        <details>
          <summary>Come si cambia il ruolo dimostrativo?</summary>
          <p>Usa la schermata di accesso per selezionare il ruolo dimostrativo. Sidebar e contenuti si adatteranno al profilo scelto.</p>
        </details>
        <details>
          <summary>Come si cambia la palette di colori dell'interfaccia?</summary>
          <p>Nell'header seleziona una palette per applicare istantaneamente le variabili CSS corrispondenti su tutta l'applicazione.</p>
        </details>
      </section>
    </div>
  );
}
