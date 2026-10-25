// Tiny localStorage "database" shared by Register, Display and Login.
export type User = {
  name: string
  email: string
  password: string
}

const KEY = 'bootstrap-demo-users'

export function getUsers(): User[] {
  try {
    return JSON.parse(localStorage.getItem(KEY) ?? '[]') as User[]
  } catch {
    return []
  }
}

export function addUser(user: User) {
  localStorage.setItem(KEY, JSON.stringify([...getUsers(), user]))
}
