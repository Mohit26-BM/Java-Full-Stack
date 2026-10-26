import { CATEGORIES, EXPERIENCE_LEVELS } from './sampleData.js'

const EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/
const PHONE = /^[6-9]\d{9}$/

export function validateParticipant(form, participants) {
  const errors = {}
  if (form.name.trim().length < 2) errors.name = form.name.trim() ? 'Name must be at least 2 characters' : 'Name is required'
  if (!form.email.trim()) errors.email = 'Email is required'
  else if (!EMAIL.test(form.email.trim())) errors.email = 'Enter a valid email, e.g. name@company.com'
  else if (participants.some((p) => p.email.toLowerCase() === form.email.trim().toLowerCase())) {
    errors.email = 'A participant with this email is already registered'
  }
  if (!form.phone) errors.phone = 'Mobile number is required'
  else if (!PHONE.test(form.phone)) errors.phone = 'Mobile number must be 10 digits and start with 6, 7, 8 or 9'
  if (!form.organization.trim()) errors.organization = 'Organization is required'
  if (!EXPERIENCE_LEVELS.includes(form.level)) errors.level = 'Select an experience level'
  return errors
}

export function validateCourse(form, courses, trainers) {
  const errors = {}
  const title = form.title.trim()
  if (title.length < 3) errors.title = title ? 'Title must be at least 3 characters' : 'Title is required'
  else if (courses.some((c) => c.title.toLowerCase() === title.toLowerCase())) errors.title = 'A course with this title already exists'
  if (!CATEGORIES.includes(form.category)) errors.category = 'Select a category'
  if (!trainers.some((t) => t.id === form.trainerId)) errors.trainerId = 'Select a trainer'
  if (!/^\d{4}-\d{2}-\d{2}$/.test(form.startDate) || Number.isNaN(Date.parse(form.startDate))) errors.startDate = 'Select a start date'
  const duration = Number(form.durationDays)
  if (!Number.isInteger(duration) || duration < 1 || duration > 90) errors.durationDays = 'Duration must be 1 to 90 days'
  const capacity = Number(form.capacity)
  if (!Number.isInteger(capacity) || capacity < 1 || capacity > 100) errors.capacity = 'Capacity must be 1 to 100 seats'
  return errors
}
