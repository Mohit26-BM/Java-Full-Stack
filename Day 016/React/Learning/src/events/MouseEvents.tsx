import { useState } from "react";

export default function MouseEvents() {
  const [message, setMessage] = useState("Move the pointer over this box.");

  return (
    <div
      onMouseEnter={() => setMessage("Pointer entered the box.")}
      onMouseLeave={() => setMessage("Pointer left the box.")}
      style={{ border: "1px solid #777", padding: 24 }}
    >
      <p>{message}</p>
    </div>
  );
}
