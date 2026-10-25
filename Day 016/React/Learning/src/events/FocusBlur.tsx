import { useState } from "react";

export default function FocusBlur() {
  const [message, setMessage] = useState("Input is not focused.");

  return (
    <div>
      <input
        onFocus={() => setMessage("Input is focused.")}
        onBlur={() => setMessage("Input lost focus.")}
        placeholder="Click or tab into this input"
      />
      <p>{message}</p>
    </div>
  );
}
