import { useState } from 'react'

function TaskForm({ onAdd }) {
  const [text, setText] = useState('')

  const handleSubmit = (event) => {
    event.preventDefault()
    if (!text.trim()) return
    onAdd(text.trim())
    setText('')
  }

  return (
    <form className="todo__form" onSubmit={handleSubmit}>
      <input
        className="todo__input"
        value={text}
        onChange={(event) => setText(event.target.value)}
        placeholder="New task description"
        aria-label="New task"
      />
      <button type="submit" className="todo__button todo__button--primary">Add</button>
    </form>
  )
}

export default TaskForm
