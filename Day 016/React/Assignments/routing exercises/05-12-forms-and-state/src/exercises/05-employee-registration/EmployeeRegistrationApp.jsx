import { useState } from 'react'
import EmployeeForm from './EmployeeForm.jsx'
import SubmittedEmployees from './SubmittedEmployees.jsx'

function EmployeeRegistrationApp() {
  const [employees, setEmployees] = useState([])

  const addEmployee = (employee) => {
    setEmployees((current) => [...current, { ...employee, id: current.length + 1 }])
  }

  return (
    <section>
      <h1 className="page-title">Employee Registration</h1>
      <EmployeeForm onSubmit={addEmployee} />
      <SubmittedEmployees employees={employees} />
    </section>
  )
}

export default EmployeeRegistrationApp
