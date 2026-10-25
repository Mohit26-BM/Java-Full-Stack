function SearchBar({ value, onChange }) {
  return (
    <label className="employee-search__field">
      Search by name or ID
      <input
        type="search"
        className="employee-search__input"
        value={value}
        onChange={(event) => onChange(event.target.value)}
        placeholder="e.g. asha or EMP-104"
      />
    </label>
  )
}

export default SearchBar
