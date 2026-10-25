export default function Propagation() {
  return (
    <div
      onClick={() => alert("Parent clicked")}
      style={{ padding: 24, border: "1px solid gray" }}
    >
      {/* Bubbles: shows button alert, then parent alert */}
      <button onClick={() => alert("Button clicked")}>Bubbles</button>

      {/* stopPropagation: parent handler is not called */}
      <button
        onClick={event => {
          event.stopPropagation();
          alert("Button only");
        }}
      >
        Stop bubbling
      </button>
    </div>
  );
}
