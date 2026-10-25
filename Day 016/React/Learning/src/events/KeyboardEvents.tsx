import { useState } from "react";
import type { KeyboardEvent } from "react";

export default function KeyboardEvents() {
  const [message, setMessage] = useState("Type in the input.");

  function handleKeyDown(event: KeyboardEvent<HTMLInputElement>) {
    setMessage(`Key pressed: ${event.key}`);
  }

  return (
    <div>
      <input onKeyDown={handleKeyDown} placeholder="Press a key" />
      <p>{message}</p>
    </div>
  );
}
