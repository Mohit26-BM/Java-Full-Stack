import { useState } from "react";
import type { ChangeEvent } from "react";

export default function TextInput() {
  const [name, setName] = useState<string>("");

  function handleChange(event: ChangeEvent<HTMLInputElement>) {
    setName(event.target.value);
  }

  return (
    <div>
      <input value={name} onChange={handleChange} placeholder="Enter your name" />
      <p>Hello, {name || "Guest"}!</p>
    </div>
  );
}
