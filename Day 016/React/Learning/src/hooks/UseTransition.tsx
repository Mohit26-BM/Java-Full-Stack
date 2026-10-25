import { useMemo, useState, useTransition } from "react";

const words = Array.from({ length: 5000 }, (_, index) => `Product ${index + 1}`);

export default function UseTransition() {
  const [input, setInput] = useState("");
  const [query, setQuery] = useState("");
  const [isPending, startTransition] = useTransition();

  const results = useMemo(
    () => words.filter(word => word.toLowerCase().includes(query.toLowerCase())),
    [query]
  );

  function handleChange(value: string) {
    setInput(value); // urgent
    startTransition(() => {
      setQuery(value); // non-urgent
    });
  }

  return (
    <div>
      <input value={input} onChange={event => handleChange(event.target.value)} />
      {isPending && <p>Updating results...</p>}
      <ul>{results.slice(0, 10).map(word => <li key={word}>{word}</li>)}</ul>
    </div>
  );
}
