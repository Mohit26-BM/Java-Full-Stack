import { memo, useCallback, useState } from "react";

const IncrementButton = memo(function IncrementButton({ onIncrement }: { onIncrement: () => void }) {
  console.log("IncrementButton rendered");
  return <button onClick={onIncrement}>Increment child count</button>;
});

export default function UseCallback() {
  const [count, setCount] = useState(0);
  const [dark, setDark] = useState(false);

  const increment = useCallback(() => {
    setCount(current => current + 1);
  }, []);

  return (
    <div>
      <p>Count: {count} | Theme: {dark ? "dark" : "light"}</p>
      <IncrementButton onIncrement={increment} />
      {/* re-renders parent, but the child does not re-render (check console) */}
      <button onClick={() => setDark(current => !current)}>Toggle theme</button>
    </div>
  );
}
