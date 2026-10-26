import { createContext, useContext, useState } from 'react'

export const DEPARTMENTS = ['Engineering', 'Finance', 'Human Resources', 'Sales', 'Quality']

export const SAMPLE_EMPLOYEES = [
  { id: 1, name: 'Asha Verma', email: 'asha.verma@company.com', department: 'Engineering', designation: 'Software Engineer', salary: 95000 },
  { id: 2, name: 'Ravi Iyer', email: 'ravi.iyer@company.com', department: 'Engineering', designation: 'Backend Developer', salary: 92000 },
  { id: 3, name: 'Hana Kapoor', email: 'hana.kapoor@company.com', department: 'Human Resources', designation: 'HR Manager', salary: 140000 },
  { id: 4, name: 'Karan Shah', email: 'karan.shah@company.com', department: 'Finance', designation: 'Accountant', salary: 80000 },
]

const EmployeeContext = createContext(null)

export function EmployeeProvider({ children, initialEmployees = SAMPLE_EMPLOYEES }) {
  const [employees, setEmployees] = useState(initialEmployees)

  const addEmployee = (data) => {
    const id = Math.max(0, ...employees.map((e) => e.id)) + 1
    setEmployees([...employees, { ...data, id }])
    return id
  }

  const updateEmployee = (id, data) => {
    setEmployees(employees.map((e) => (e.id === id ? { ...data, id } : e)))
  }

  const deleteEmployee = (id) => {
    setEmployees(employees.filter((e) => e.id !== id))
  }

  const findEmployee = (idParam) => (/^\d+$/.test(idParam ?? '') ? employees.find((e) => e.id === Number(idParam)) : undefined)

  return (
    <EmployeeContext.Provider value={{ employees, addEmployee, updateEmployee, deleteEmployee, findEmployee }}>
      {children}
    </EmployeeContext.Provider>
  )
}

export function useEmployees() {
  const context = useContext(EmployeeContext)
  if (!context) throw new Error('useEmployees must be used inside <EmployeeProvider>')
  return context
}
