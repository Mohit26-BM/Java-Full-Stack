import { useState } from "react";

interface Task {
  id: number;
  title: string;
}

export default function AddItem() {
  const [tasks, setTasks] = useState<Task[]>([
    { id: 1, title: "Learn JSX" },
    { id: 2, title: "Learn props" }
  ]);
  const [title, setTitle] = useState<string>("");

  function addTask() {
    const trimmed = title.trim();
    if (!trimmed) return;

    setTasks(previous => [...previous, { id: Date.now(), title: trimmed }]);
    setTitle("");
  }

  return (
    <div>
      <input
        value={title}
        onChange={event => setTitle(event.target.value)}
        placeholder="New task"
      />
      <button onClick={addTask}>Add task</button>
      <ul>{tasks.map(task => <li key={task.id}>{task.title}</li>)}</ul>
    </div>
  );
}
