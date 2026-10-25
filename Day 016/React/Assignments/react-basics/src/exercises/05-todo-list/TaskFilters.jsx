function TaskFilters({ filters, selected, onSelect }) {
  return (
    <div className="todo__filters" role="group" aria-label="Show tasks">
      {filters.map((name) => (
        <button
          key={name}
          type="button"
          className={name === selected ? 'todo__filter todo__filter--active' : 'todo__filter'}
          aria-pressed={name === selected}
          onClick={() => onSelect(name)}
        >
          {name}
        </button>
      ))}
    </div>
  )
}

export default TaskFilters
