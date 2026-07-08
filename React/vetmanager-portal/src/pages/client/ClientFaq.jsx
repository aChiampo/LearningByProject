import PageTitle from '../../components/common/PageTitle';

export default function ClientFaq() {
  return (
    <div>
      <PageTitle eyebrow="Supporto cliente" title="FAQ" />
      <section className="panel faq-list">
        <details>
          <summary>Come prenoto una visita?</summary>
          <p>Apri "Prenota appuntamento" dalla barra laterale, scegli l'animale e conferma.</p>
        </details>
      </section>
    </div>
  );
}
