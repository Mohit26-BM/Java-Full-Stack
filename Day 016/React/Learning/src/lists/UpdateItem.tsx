import { useState } from "react";

interface Task {
  id: number;
  title: string;
  completed: boolean;
}

export default function UpdateItem() {
  const [tasks, setTasks] = useState<Task[]>([
    { id: 1, title: "Learn React", completed: false },
    { id: 2, title: "Practice TypeScript", completed: false }
  ]);

  function toggleTask(id: number) {
    setTasks(previous =>
      previous.map(task =>
        task.id === id ? { ...task, completed: !task.completed } : task
      )
    );
  }

  return (
    <ul>
      {tasks.map(task => (
        <li key={task.id}>
          <label>
            <input
              type="checkbox"
              checked={task.completed}
              onChange={() => toggleTask(task.id)}
            />
            {task.completed ? <s>{task.title}</s> : task.title}
          </label>
        </li>
      ))}
    </ul>
  );
}
