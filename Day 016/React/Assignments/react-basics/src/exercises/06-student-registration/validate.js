export const COURSES = ['Computer Science', 'Data Science', 'Electronics', 'Mechanical', 'Business Administration']

export const EMPTY_FORM = { name: '', email: '', age: '', course: '', mobile: '' }

const EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/
const MOBILE = /^\d{10}$/

export function validate(form) {
  const errors = {}

  if (!form.name.trim()) {
    errors.name = 'Name is required'
  }

  if (!form.email.trim()) {
    errors.email = 'Email is required'
  } else if (!EMAIL.test(form.email.trim())) {
    errors.email = 'Enter a valid email, e.g. name@example.com'
  }

  if (form.age === '') {
    errors.age = 'Age is required'
  } else {
    const age = Number(form.age)
    if (!Number.isInteger(age) || age < 18 || age > 60) {
      errors.age = 'Age must be a whole number between 18 and 60'
    }
  }

  if (!form.course) {
    errors.course = 'Please select a course'
  }

  if (!form.mobile) {
    errors.mobile = 'Mobile number is required'
  } else if (!MOBILE.test(form.mobile)) {
    errors.mobile = 'Mobile number must be exactly 10 digits'
  }

  return errors
}
