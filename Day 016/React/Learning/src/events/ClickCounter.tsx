import { useState } from "react";

export default function ClickCounter() {
  const [count, setCount] = useState<number>(0);

  return (
    <div>
      <h2>Count: {count}</h2>
      <button onClick={() => setCount(previous => previous + 1)}>+</button>
      <button onClick={() => setCount(previous => previous - 1)}>-</button>
      <button onClick={() => setCount(0)}>Reset</button>
    </div>
  );
}
