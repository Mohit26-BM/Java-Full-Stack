export const required = (value, label) => (String(value ?? '').trim() ? '' : `${label} is required`)

export const email = (value) =>
  /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(value.trim()) ? '' : 'Enter a valid email, e.g. name@example.com'

export const mobile = (value) =>
  /^[6-9]\d{9}$/.test(value) ? '' : 'Mobile number must be 10 digits and start with 6, 7, 8 or 9'

export const minLength = (value, min, label) =>
  value.length >= min ? '' : `${label} must be at least ${min} characters`

export const positiveNumber = (value, label) =>
  Number(value) > 0 ? '' : `${label} must be greater than 0`

export const firstError = (...messages) => messages.find(Boolean) ?? ''

export const onlyErrors = (errors) => Object.fromEntries(Object.entries(errors).filter(([, message]) => message))
