const EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/

export function validateEmployee(form, otherEmployees) {
  const errors = {}
  if (!form.name.trim()) errors.name = 'Name is required'
  else if (form.name.trim().length < 2) errors.name = 'Name must be at least 2 characters'

  if (!form.email.trim()) errors.email = 'Email is required'
  else if (!EMAIL.test(form.email.trim())) errors.email = 'Enter a valid email, e.g. name@company.com'
  else if (otherEmployees.some((e) => e.email.toLowerCase() === form.email.trim().toLowerCase())) {
    errors.email = 'Another employee already uses this email'
  }

  if (!form.department) errors.department = 'Select a department'
  if (!form.designation.trim()) errors.designation = 'Designation is required'

  const salary = Number(form.salary)
  if (form.salary === '' || form.salary === null) errors.salary = 'Salary is required'
  else if (!Number.isFinite(salary) || salary <= 0) errors.salary = 'Salary must be greater than 0'
  else if (salary > 10000000) errors.salary = 'Salary must be at most ₹1,00,00,000'

  return errors
}
