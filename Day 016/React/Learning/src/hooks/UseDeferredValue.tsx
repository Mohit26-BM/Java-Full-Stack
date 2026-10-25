import { memo, useDeferredValue, useMemo, useState } from "react";

const products = Array.from({ length: 4000 }, (_, index) => `Product ${index + 1}`);

const Results = memo(function Results({ query }: { query: string }) {
  const results = useMemo(
    () => products.filter(item => item.toLowerCase().includes(query.toLowerCase())),
    [query]
  );

  return <ul>{results.slice(0, 10).map(item => <li key={item}>{item}</li>)}</ul>;
});

export default function UseDeferredValue() {
  const [query, setQuery] = useState("");
  const deferredQuery = useDeferredValue(query);

  return (
    <div>
      <input value={query} onChange={event => setQuery(event.target.value)} />
      <Results query={deferredQuery} />
    </div>
  );
}
