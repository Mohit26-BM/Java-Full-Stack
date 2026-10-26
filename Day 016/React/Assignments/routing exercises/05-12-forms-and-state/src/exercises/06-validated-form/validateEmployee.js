import { email, firstError, minLength, mobile, onlyErrors, required } from '../../shared/validators.js'

export const MIN_PASSWORD = 8

export function validateEmployee(form) {
  return onlyErrors({
    name: required(form.name, 'Name'),
    email: firstError(required(form.email, 'Email'), email(form.email)),
    mobile: firstError(required(form.mobile, 'Mobile number'), mobile(form.mobile)),
    password: firstError(required(form.password, 'Password'), minLength(form.password, MIN_PASSWORD, 'Password')),
    confirmPassword: firstError(
      required(form.confirmPassword, 'Confirm password'),
      form.confirmPassword === form.password ? '' : 'Passwords do not match',
    ),
  })
}
