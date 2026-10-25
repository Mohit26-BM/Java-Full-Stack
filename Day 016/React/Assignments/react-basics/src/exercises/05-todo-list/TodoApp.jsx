import { useState } from 'react'
import TaskFilters from './TaskFilters.jsx'
import TaskForm from './TaskForm.jsx'
import TaskList from './TaskList.jsx'
import './Todo.css'

let nextId = 1
const newId = () => `${Date.now()}-${nextId++}`

export const FILTERS = {
  All: () => true,
  Active: (task) => !task.completed,
  Completed: (task) => task.completed,
}

function TodoApp() {
  const [tasks, setTasks] = useState([])
  const [filter, setFilter] = useState('All')

  const addTask = (text) => {
    setTasks((current) => [...current, { id: newId(), text, completed: false }])
  }

  const toggleTask = (id) => {
    setTasks((current) => current.map((task) => (task.id === id ? { ...task, completed: !task.completed } : task)))
  }

  const editTask = (id, text) => {
    setTasks((current) => current.map((task) => (task.id === id ? { ...task, text } : task)))
  }

  const deleteTask = (id) => {
    setTasks((current) => current.filter((task) => task.id !== id))
  }

  const pending = tasks.filter((task) => !task.completed).length
  const visible = tasks.filter(FILTERS[filter])

  return (
    <section className="todo">
      <h1 className="page-title">Team To-Do List</h1>
      <TaskForm onAdd={addTask} />
      <div className="todo__toolbar">
        <p className="todo__pending">{pending} pending {pending === 1 ? 'task' : 'tasks'}</p>
        <TaskFilters filters={Object.keys(FILTERS)} selected={filter} onSelect={setFilter} />
      </div>
      {visible.length === 0
        ? <p className="todo__empty">{tasks.length === 0 ? 'No tasks yet. Add one above.' : `No ${filter.toLowerCase()} tasks.`}</p>
        : <TaskList tasks={visible} onToggle={toggleTask} onEdit={editTask} onDelete={deleteTask} />}
    </section>
  )
}

export default TodoApp
