import { useState } from 'react'
import DepartmentFilter from './DepartmentFilter.jsx'
import EmployeeTable from './EmployeeTable.jsx'
import SearchBar from './SearchBar.jsx'
import { employees } from './employees.js'
import { ALL_DEPARTMENTS, departmentsOf, filterEmployees } from './filterEmployees.js'
import './EmployeeSearch.css'

function EmployeeSearchApp() {
  const [search, setSearch] = useState('')
  const [department, setDepartment] = useState(ALL_DEPARTMENTS)

  const matches = filterEmployees(employees, search, department)

  return (
    <section>
      <h1 className="page-title">Employee Search</h1>
      <div className="employee-search__filters">
        <SearchBar value={search} onChange={setSearch} />
        <DepartmentFilter departments={departmentsOf(employees)} value={department} onChange={setDepartment} />
      </div>
      <p className="employee-search__count">{matches.length} of {employees.length} employees</p>
      {matches.length === 0
        ? <p className="employee-search__empty">No employees found</p>
        : <EmployeeTable employees={matches} />}
    </section>
  )
}

export default EmployeeSearchApp
