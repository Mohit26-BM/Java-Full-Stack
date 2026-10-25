import { useState } from "react";
import type { FormEvent } from "react";

export default function FormSubmit() {
  const [email, setEmail] = useState<string>("");
  const [message, setMessage] = useState<string>("");

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!email.trim().includes("@")) {
      setMessage("Please enter an email containing @.");
      return;
    }
    setMessage(`Form submitted for ${email}.`);
  }

  return (
    <form onSubmit={handleSubmit}>
      <input value={email} onChange={event => setEmail(event.target.value)} />
      <button type="submit">Submit</button>
      <p>{message}</p>
    </form>
  );
}
