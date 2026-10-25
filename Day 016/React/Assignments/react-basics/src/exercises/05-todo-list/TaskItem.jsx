import { useState } from 'react'

function TaskItem({ task, onToggle, onEdit, onDelete }) {
  const [editing, setEditing] = useState(false)
  const [draft, setDraft] = useState(task.text)

  const save = () => {
    if (!draft.trim()) return
    onEdit(task.id, draft.trim())
    setEditing(false)
  }

  const cancel = () => {
    setDraft(task.text)
    setEditing(false)
  }

  return (
    <li className={task.completed ? 'todo__item todo__item--done' : 'todo__item'}>
      <input
        type="checkbox"
        checked={task.completed}
        onChange={() => onToggle(task.id)}
        aria-label={`Complete ${task.text}`}
      />
      {editing ? (
        <>
          <input
            className="todo__input todo__input--edit"
            value={draft}
            onChange={(event) => setDraft(event.target.value)}
            aria-label={`Edit ${task.text}`}
          />
          <button type="button" className="todo__button" onClick={save}>Save</button>
          <button type="button" className="todo__button" onClick={cancel}>Cancel</button>
        </>
      ) : (
        <>
          <span className="todo__text">{task.text}</span>
          <button type="button" className="todo__button" onClick={() => setEditing(true)}>Edit</button>
          <button type="button" className="todo__button todo__button--danger" onClick={() => onDelete(task.id)}>Delete</button>
        </>
      )}
    </li>
  )
}

export default TaskItem
