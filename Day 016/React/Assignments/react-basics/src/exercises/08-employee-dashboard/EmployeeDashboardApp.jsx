import { useState } from 'react'
import DepartmentFilter from './DepartmentFilter.jsx'
import EmployeeList from './EmployeeList.jsx'
import EmployeeSummary from './EmployeeSummary.jsx'
import Header from './Header.jsx'
import Statistics from './Statistics.jsx'
import { employees } from './employees.js'
import { ALL, SALARY_RANGES, SORTS, departmentsOf, selectEmployees } from './dashboardData.js'
import './EmployeeDashboard.css'

function EmployeeDashboardApp() {
  const [department, setDepartment] = useState(ALL)
  const [salaryRange, setSalaryRange] = useState('Any')
  const [sortBy, setSortBy] = useState('Name (A–Z)')

  const matching = selectEmployees(employees, department, salaryRange, sortBy)

  return (
    <section className="dashboard">
      <Header title="Employee Dashboard" />
      <DepartmentFilter
        departments={departmentsOf(employees)}
        department={department}
        onDepartmentChange={setDepartment}
        salaryRanges={Object.keys(SALARY_RANGES)}
        salaryRange={salaryRange}
        onSalaryRangeChange={setSalaryRange}
        sorts={Object.keys(SORTS)}
        sortBy={sortBy}
        onSortChange={setSortBy}
      />
      <Statistics total={employees.length} matching={matching} />
      <EmployeeSummary department={department} shown={matching.length} total={employees.length} />
      <EmployeeList employees={matching} />
    </section>
  )
}

export default EmployeeDashboardApp
