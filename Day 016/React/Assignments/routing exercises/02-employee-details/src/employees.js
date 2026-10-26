export const initialEmployees = [
  { id: 101, name: 'Asha Verma', email: 'asha.verma@company.com', department: 'Engineering', role: 'Software Engineer', location: 'Bengaluru' },
  { id: 102, name: 'Ravi Iyer', email: 'ravi.iyer@company.com', department: 'Engineering', role: 'Backend Developer', location: 'Chennai' },
  { id: 103, name: 'Hana Kapoor', email: 'hana.kapoor@company.com', department: 'Human Resources', role: 'HR Manager', location: 'Mumbai' },
  { id: 104, name: 'Karan Shah', email: 'karan.shah@company.com', department: 'Finance', role: 'Accountant', location: 'Pune' },
  { id: 105, name: 'Priya Menon', email: 'priya.menon@company.com', department: 'Sales', role: 'Account Executive', location: 'Delhi' },
  { id: 106, name: 'Neha Joshi', email: 'neha.joshi@company.com', department: 'Quality', role: 'QA Engineer', location: 'Hyderabad' },
]

export function findEmployee(employees, employeeId) {
  if (!/^\d+$/.test(employeeId ?? '')) return undefined
  return employees.find((employee) => employee.id === Number(employeeId))
}
