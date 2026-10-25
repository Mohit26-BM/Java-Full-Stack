export const ALL_DEPARTMENTS = 'All'

export function filterEmployees(employees, search, department) {
  const term = search.trim().toLowerCase()
  return employees.filter((employee) => {
    const matchesText = employee.name.toLowerCase().includes(term) || employee.id.toLowerCase().includes(term)
    const matchesDepartment = department === ALL_DEPARTMENTS || employee.department === department
    return matchesText && matchesDepartment
  })
}

export function departmentsOf(employees) {
  return [ALL_DEPARTMENTS, ...new Set(employees.map((employee) => employee.department))]
}
