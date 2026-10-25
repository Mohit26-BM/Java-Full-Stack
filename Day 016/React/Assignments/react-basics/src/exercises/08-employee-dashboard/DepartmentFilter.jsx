function Select({ label, value, options, onChange }) {
  return (
    <label className="dashboard__field">
      {label}
      <select className="dashboard__select" value={value} onChange={(event) => onChange(event.target.value)}>
        {options.map((option) => <option key={option} value={option}>{option}</option>)}
      </select>
    </label>
  )
}

function DepartmentFilter({ departments, department, onDepartmentChange,
                            salaryRanges, salaryRange, onSalaryRangeChange,
                            sorts, sortBy, onSortChange }) {
  return (
    <div className="dashboard__filters">
      <Select label="Department" value={department} options={departments} onChange={onDepartmentChange} />
      <Select label="Salary range" value={salaryRange} options={salaryRanges} onChange={onSalaryRangeChange} />
      <Select label="Sort by" value={sortBy} options={sorts} onChange={onSortChange} />
    </div>
  )
}

export default DepartmentFilter
