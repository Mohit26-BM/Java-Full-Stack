import { useState } from "react";

interface Task {
  id: number;
  title: string;
}

export default function RemoveItem() {
  const [tasks, setTasks] = useState<Task[]>([
    { id: 1, title: "Learn events" },
    { id: 2, title: "Learn lists" },
    { id: 3, title: "Practice React" }
  ]);

  function removeTask(id: number) {
    setTasks(previous => previous.filter(task => task.id !== id));
  }

  return (
    <ul>
      {tasks.map(task => (
        <li key={task.id}>
          {task.title}
          <button onClick={() => removeTask(task.id)}>Remove</button>
        </li>
      ))}
    </ul>
  );
}
