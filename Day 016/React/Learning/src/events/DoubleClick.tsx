export default function DoubleClick() {
  return (
    <button onDoubleClick={() => alert("Double-click detected!")}>
      Double-click me
    </button>
  );
}
