import { useEffect, useState } from "react";

export default function UseEffect() {
  const [count, setCount] = useState(0);
  const [seconds, setSeconds] = useState(0);

  // runs when count changes
  useEffect(() => {
    document.title = `Count: ${count}`;
  }, [count]);

  // runs once, cleanup on unmount
  useEffect(() => {
    const timerId = window.setInterval(() => {
      setSeconds(previous => previous + 1);
    }, 1000);
    return () => window.clearInterval(timerId);
  }, []);

  return (
    <div>
      <button onClick={() => setCount(count + 1)}>Count: {count}</button>
      <p>Seconds elapsed: {seconds}</p>
    </div>
  );
}
