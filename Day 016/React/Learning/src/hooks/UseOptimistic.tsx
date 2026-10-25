import { useOptimistic, useState, useTransition } from "react";

type Message = { id: number; text: string };

export default function UseOptimistic() {
  const [messages, setMessages] = useState<Message[]>([{ id: 1, text: "Welcome!" }]);

  const [optimisticMessages, addOptimisticMessage] = useOptimistic(
    messages,
    (current, newText: string) => [...current, { id: Date.now(), text: newText }]
  );

  const [, startTransition] = useTransition();

  function sendMessage(formData: FormData) {
    const text = String(formData.get("message") ?? "").trim();
    if (!text) return;

    startTransition(async () => {
      addOptimisticMessage(text); // shows immediately
      await new Promise(resolve => setTimeout(resolve, 700)); // fake request
      setMessages(current => [...current, { id: Date.now(), text }]);
    });
  }

  return (
    <div>
      <form action={sendMessage}>
        <input name="message" required placeholder="Type a message" />
        <button>Send</button>
      </form>
      <ul>
        {optimisticMessages.map(message => (
          <li key={message.id}>{message.text}</li>
        ))}
      </ul>
    </div>
  );
}
