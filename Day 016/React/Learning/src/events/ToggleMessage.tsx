import { useState } from "react";

export default function ToggleMessage() {
  const [visible, setVisible] = useState<boolean>(false);

  return (
    <div>
      <button onClick={() => setVisible(previous => !previous)}>
        {visible ? "Hide" : "Show"}
      </button>
      {visible && <p>The message is visible now.</p>}
    </div>
  );
}
