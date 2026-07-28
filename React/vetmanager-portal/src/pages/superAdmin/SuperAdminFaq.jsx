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
          <summary>Quale palette di colori usa l'interfaccia?</summary>
          <p>L'interfaccia usa la palette Cielo in modo predefinito per tutti gli utenti.</p>
        </details>
      </section>
    </div>
  );
}
