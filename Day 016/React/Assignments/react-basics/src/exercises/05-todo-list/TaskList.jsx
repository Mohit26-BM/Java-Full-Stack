import TaskItem from './TaskItem.jsx'

function TaskList({ tasks, onToggle, onEdit, onDelete }) {
  return (
    <ul className="todo__list">
      {tasks.map((task) => (
        <TaskItem key={task.id} task={task} onToggle={onToggle} onEdit={onEdit} onDelete={onDelete} />
      ))}
    </ul>
  )
}

export default TaskList
