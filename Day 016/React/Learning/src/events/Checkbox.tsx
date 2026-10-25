import { useState } from "react";

export default function Checkbox() {
  const [accepted, setAccepted] = useState<boolean>(false);

  return (
    <div>
      <label>
        <input
          type="checkbox"
          checked={accepted}
          onChange={event => setAccepted(event.target.checked)}
        />
        I accept the terms.
      </label>
      <button disabled={!accepted}>Continue</button>
    </div>
  );
}
