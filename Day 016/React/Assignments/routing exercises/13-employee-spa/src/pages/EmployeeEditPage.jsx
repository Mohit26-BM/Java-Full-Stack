import { useNavigate, useParams } from 'react-router-dom'
import EmployeeForm from '../components/EmployeeForm.jsx'
import EmployeeNotFound from '../components/EmployeeNotFound.jsx'
import { useEmployees } from '../context/EmployeeContext.jsx'

function EmployeeEditPage() {
  const { id } = useParams()
  const { employees, findEmployee, updateEmployee } = useEmployees()
  const navigate = useNavigate()
  const employee = findEmployee(id)

  if (!employee) return <EmployeeNotFound id={id} />

  const handleSubmit = (data) => {
    updateEmployee(employee.id, data)
    navigate(`/employees/${employee.id}`, { state: { message: 'Changes saved.' } })
  }

  return (
    <section className="narrow">
      <h1 className="page-title">Edit {employee.name}</h1>
      <EmployeeForm
        key={employee.id}
        initial={employee}
        otherEmployees={employees.filter((e) => e.id !== employee.id)}
        submitLabel="Save changes"
        cancelTo={`/employees/${employee.id}`}
        onSubmit={handleSubmit}
      />
    </section>
  )
}

export default EmployeeEditPage
