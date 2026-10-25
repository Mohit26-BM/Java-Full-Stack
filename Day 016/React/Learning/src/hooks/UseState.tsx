import { useState } from "react";

export default function UseState() {
  const [count, setCount] = useState<number>(0);

  return (
    <div>
      <p>Count: {count}</p>
      <button onClick={() => setCount(previous => previous + 1)}>+</button>
      <button onClick={() => setCount(previous => previous - 1)}>-</button>
      <button onClick={() => setCount(0)}>Reset</button>
    </div>
  );
}
