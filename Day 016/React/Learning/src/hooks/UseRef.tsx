import { useRef } from "react";

export default function UseRef() {
  const inputRef = useRef<HTMLInputElement>(null);

  return (
    <div>
      <input ref={inputRef} placeholder="Search..." />
      <button onClick={() => inputRef.current?.focus()}>Focus search</button>
    </div>
  );
}
