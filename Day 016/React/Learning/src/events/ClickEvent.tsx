export default function ClickEvent() {
  function handleClick() {
    alert("The button was clicked!");
  }

  return <button onClick={handleClick}>Click Me</button>;
}
