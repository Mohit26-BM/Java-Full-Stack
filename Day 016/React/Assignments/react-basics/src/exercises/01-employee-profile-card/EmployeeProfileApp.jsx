import EmployeeList from './EmployeeList.jsx'
import { employees } from './employees.js'

function EmployeeProfileApp() {
  return (
    <section>
      <h1 className="page-title">Employee Profiles</h1>
      <EmployeeList employees={employees} />
    </section>
  )
}

export default EmployeeProfileApp
