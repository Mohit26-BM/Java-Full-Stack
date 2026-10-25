export default function PreventDefault() {
  return (
    <a
      href="https://example.com"
      onClick={event => {
        event.preventDefault();
        alert("Navigation was prevented.");
      }}
    >
      Click this link
    </a>
  );
}
