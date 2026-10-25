export default function InlineAndArgs() {
  function greet(name: string) {
    alert(`Hello, ${name}!`);
  }

  return (
    <div>
      <button onClick={() => alert("Hello from React!")}>Inline</button>
      <button onClick={() => greet("Ananya")}>Greet Ananya</button>
      <button onClick={() => greet("Rahul")}>Greet Rahul</button>
    </div>
  );
}
