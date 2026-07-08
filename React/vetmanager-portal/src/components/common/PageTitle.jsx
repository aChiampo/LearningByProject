export default function PageTitle({ eyebrow, title }) {
  return (
    <div className="page-title">
      {eyebrow && <p className="eyebrow">{eyebrow}</p>}
      <h1>{title}</h1>
    </div>
  );
}
