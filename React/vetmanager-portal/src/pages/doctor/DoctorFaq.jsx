import PageTitle from '../../components/common/PageTitle';

export default function DoctorFaq() {
  return (
    <div>
      <PageTitle eyebrow="Supporto Medico" title="FAQ Veterinario" />
      <section className="panel faq-list section-spaced-sm">
        <details>
          <summary>Come trovo la cartella clinica di un animale?</summary>
          <p>Vai su "Gestisci animali" dalla barra laterale, inserisci il nome del paziente e clicca su "Vedi Cartella".</p>
        </details>
        <details>
          <summary>Come inserisco un nuovo referto?</summary>
          <p>Dentro la cartella clinica dell'animale, clicca su "Aggiungi Referto", compila il testo della diagnosi e premi salva.</p>
        </details>
      </section>
    </div>
  );
}
