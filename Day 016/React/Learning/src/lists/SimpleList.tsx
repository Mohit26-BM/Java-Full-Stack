export default function SimpleList() {
  const fruits: string[] = ["Apple", "Banana", "Mango", "Orange"];

  return (
    <ul>
      {fruits.map(fruit => <li key={fruit}>{fruit}</li>)}
    </ul>
  );
}
