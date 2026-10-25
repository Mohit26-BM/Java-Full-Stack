import { useDebugValue, useState } from "react";

function useDemoStatus() {
  const [online, setOnline] = useState(true);
  useDebugValue(online ? "Online" : "Offline"); // visible in React DevTools
  return { online, setOnline };
}

export default function UseDebugValue() {
  const { online, setOnline } = useDemoStatus();

  return (
    <button onClick={() => setOnline(value => !value)}>
      Status: {online ? "Online" : "Offline"}
    </button>
  );
}
