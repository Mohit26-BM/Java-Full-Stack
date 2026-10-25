const CUSTOMER_API = import.meta.env.VITE_CUSTOMER_API ?? 'http://localhost:8081'
const ACCOUNT_API = import.meta.env.VITE_ACCOUNT_API ?? 'http://localhost:8082'

let token = null
let onUnauthorized = () => {}

export function setToken(value) {
  token = value
}

export function setUnauthorizedHandler(handler) {
  onUnauthorized = handler
}

async function request(base, path, { method = 'GET', body } = {}) {
  const headers = {}
  if (body !== undefined) headers['Content-Type'] = 'application/json'
  if (token) headers.Authorization = `Bearer ${token}`

  let response
  try {
    response = await fetch(`${base}${path}`, {
      method,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    })
  } catch {
    throw new Error('Cannot reach the server. Make sure the services are running.')
  }

  const data = await response.json().catch(() => null)
  if (response.ok) return data

  // A 401 while holding a token means it expired or is invalid; a failed login has no token yet.
  if (response.status === 401 && token) onUnauthorized()
  if (response.status === 403) throw new Error('You are not allowed to do that.')
  throw new Error(data?.error ?? `Request failed (${response.status})`)
}

export const api = {
  login: (credentials) => request(CUSTOMER_API, '/api/v1/auth/login', { method: 'POST', body: credentials }),
  register: (payload) => request(CUSTOMER_API, '/api/v1/auth/register', { method: 'POST', body: payload }),

  customers: () => request(CUSTOMER_API, '/api/v1/customers'),
  customer: (id) => request(CUSTOMER_API, `/api/v1/customers/${id}`),
  addCustomer: (payload) => request(CUSTOMER_API, '/api/v1/customers', { method: 'POST', body: payload }),
  savings: (id) => request(CUSTOMER_API, `/api/v1/customers/savings/${id}`),
  loans: (id) => request(CUSTOMER_API, `/api/v1/customers/loans/${id}`),

  openSavings: (payload) => request(ACCOUNT_API, '/api/v1/accounts/savings', { method: 'POST', body: payload }),
  createLoan: (payload) => request(ACCOUNT_API, '/api/v1/accounts/loans', { method: 'POST', body: payload }),
}
