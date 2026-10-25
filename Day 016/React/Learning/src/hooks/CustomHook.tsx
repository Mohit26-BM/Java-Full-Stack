import { useState } from "react";

function useCounter(initialValue = 0) {
  const [count, setCount] = useState(initialValue);

  return {
    count,
    increment: () => setCount(value => value + 1),
    decrement: () => setCount(value => value - 1),
    reset: () => setCount(initialValue)
  };
}

export default function CustomHook() {
  const first = useCounter(0);
  const second = useCounter(10);

  return (
    <div>
      <p>First: {first.count}</p>
      <button onClick={first.increment}>First +</button>
      <p>Second: {second.count}</p>
      <button onClick={second.increment}>Second +</button>
      <button onClick={second.reset}>Reset second</button>
    </div>
  );
}
