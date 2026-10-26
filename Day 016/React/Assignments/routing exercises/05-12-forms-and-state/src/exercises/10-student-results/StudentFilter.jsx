function StudentFilter({ filters, selected, onSelect }) {
  return (
    <div className="pill-group" role="group" aria-label="Show students">
      {filters.map((name) => (
        <button key={name} type="button" aria-pressed={name === selected}
          className={name === selected ? 'pill pill--active' : 'pill'} onClick={() => onSelect(name)}>
          {name}
        </button>
      ))}
    </div>
  )
}

export default StudentFilter
