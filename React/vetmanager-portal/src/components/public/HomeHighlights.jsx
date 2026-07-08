export default function HomeHighlights({ highlights }) {
  return (
    <div className="home-highlights">
      {highlights.map((highlight) => (
        <div key={highlight.title}>
          <strong>{highlight.title}</strong>
          <span>{highlight.description}</span>
        </div>
      ))}
    </div>
  );
}
