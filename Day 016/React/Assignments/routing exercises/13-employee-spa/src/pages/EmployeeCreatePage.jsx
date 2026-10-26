import { useNavigate } from 'react-router-dom'
import EmployeeForm from '../components/EmployeeForm.jsx'
import { useEmployees } from '../context/EmployeeContext.jsx'

function EmployeeCreatePage() {
  const { employees, addEmployee } = useEmployees()
  const navigate = useNavigate()

  const handleSubmit = (data) => {
    const id = addEmployee(data)
    navigate(`/employees/${id}`, { state: { message: `${data.name} was added.` } })
  }

  return (
    <section className="narrow">
      <h1 className="page-title">Add employee</h1>
      <EmployeeForm otherEmployees={employees} submitLabel="Add employee" cancelTo="/employees" onSubmit={handleSubmit} />
    </section>
  )
}

export default EmployeeCreatePage
