import { useState } from "react";

export default function TextArea() {
  const [feedback, setFeedback] = useState<string>("");

  return (
    <div>
      <textarea
        value={feedback}
        onChange={event => setFeedback(event.target.value)}
        rows={4}
      />
      <p>Characters typed: {feedback.length}</p>
    </div>
  );
}
