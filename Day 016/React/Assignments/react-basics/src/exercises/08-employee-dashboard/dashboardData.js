export const ALL = 'All'

export const SALARY_RANGES = {
  Any: () => true,
  'Below ₹50,000': (salary) => salary < 50000,
  '₹50,000 – ₹1,00,000': (salary) => salary >= 50000 && salary <= 100000,
  'Above ₹1,00,000': (salary) => salary > 100000,
}

export const SORTS = {
  'Name (A–Z)': (a, b) => a.name.localeCompare(b.name),
  'Salary (high–low)': (a, b) => b.salary - a.salary,
  'Salary (low–high)': (a, b) => a.salary - b.salary,
}

export function departmentsOf(employees) {
  return [ALL, ...new Set(employees.map((e) => e.department))]
}

export function selectEmployees(employees, department, salaryRange, sortBy) {
  return employees
    .filter((e) => department === ALL || e.department === department)
    .filter((e) => SALARY_RANGES[salaryRange](e.salary))
    .sort(SORTS[sortBy])
}

export function averageSalary(employees) {
  return employees.length ? employees.reduce((sum, e) => sum + e.salary, 0) / employees.length : 0
}

export const formatRupees = (value) => `₹${Math.round(value).toLocaleString('en-IN')}`
