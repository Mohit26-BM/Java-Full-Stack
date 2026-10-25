export default function SortList() {
  const names = ["Ravi", "Ananya", "Kiran"];
  // copy first: sort() mutates the array it is called on
  const sortedNames = [...names].sort((a, b) => a.localeCompare(b));

  return <ol>{sortedNames.map(name => <li key={name}>{name}</li>)}</ol>;
}
