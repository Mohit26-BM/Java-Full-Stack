import { useMemo, useState } from "react";

const products = [
  { id: 1, name: "Keyboard", price: 1200 },
  { id: 2, name: "Monitor", price: 12000 },
  { id: 3, name: "Mouse", price: 700 }
];

export default function UseMemo() {
  const [query, setQuery] = useState("");

  const filtered = useMemo(
    () => products.filter(p => p.name.toLowerCase().includes(query.toLowerCase())),
    [query]
  );

  return (
    <div>
      <input
        value={query}
        onChange={event => setQuery(event.target.value)}
        placeholder="Search products"
      />
      <ul>
        {filtered.map(p => <li key={p.id}>{p.name}: ₹{p.price}</li>)}
      </ul>
    </div>
  );
}
