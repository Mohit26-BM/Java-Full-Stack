function DepartmentFilter({ departments, value, onChange }) {
  return (
    <label className="employee-search__field">
      Department
      <select className="employee-search__input" value={value} onChange={(event) => onChange(event.target.value)}>
        {departments.map((department) => (
          <option key={department} value={department}>{department}</option>
        ))}
      </select>
    </label>
  )
}

export default DepartmentFilter
