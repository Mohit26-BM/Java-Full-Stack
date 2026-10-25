import { useState } from "react";

interface Product {
  id: number;
  name: string;
  category: string;
}

export default function SearchList() {
  const [search, setSearch] = useState<string>("");

  const products: Product[] = [
    { id: 1, name: "Laptop", category: "Electronics" },
    { id: 2, name: "Mobile", category: "Electronics" },
    { id: 3, name: "Chair", category: "Furniture" }
  ];

  const results = products.filter(product =>
    `${product.name} ${product.category}`
      .toLowerCase()
      .includes(search.toLowerCase())
  );

  return (
    <div>
      <input
        value={search}
        onChange={event => setSearch(event.target.value)}
        placeholder="Search by name or category"
      />
      {results.length === 0 ? (
        <p>No matching products found.</p>
      ) : (
        <ul>
          {results.map(product => (
            <li key={product.id}>{product.name} ({product.category})</li>
          ))}
        </ul>
      )}
    </div>
  );
}
