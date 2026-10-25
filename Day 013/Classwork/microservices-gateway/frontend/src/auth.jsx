/* eslint-disable react-refresh/only-export-components */
import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'
import { api, setToken, setUnauthorizedHandler } from './api.js'

const STORAGE_KEY = 'csb.session'
const AuthContext = createContext(null)

function loadSession() {
  try {
    const session = JSON.parse(localStorage.getItem(STORAGE_KEY))
    if (!session?.token) return null
    const { exp } = JSON.parse(atob(session.token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')))
    return exp * 1000 > Date.now() ? session : null
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [session, setSession] = useState(() => {
    const stored = loadSession()
    setToken(stored?.token ?? null)
    return stored
  })

  const logout = useCallback(() => {
    setToken(null)
    setSession(null)
    try {
      localStorage.removeItem(STORAGE_KEY)
    } catch {
      /* storage unavailable */
    }
  }, [])

  useEffect(() => {
    setUnauthorizedHandler(logout)
  }, [logout])

  const establish = useCallback((auth) => {
    setToken(auth.token)
    setSession(auth)
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(auth))
    } catch {
      /* storage unavailable */
    }
    return auth
  }, [])

  const login = useCallback(async (credentials) => establish(await api.login(credentials)), [establish])
  const register = useCallback(async (payload) => establish(await api.register(payload)), [establish])

  const value = useMemo(
    () => ({ user: session, isAdmin: session?.role === 'ADMIN', login, register, logout }),
    [session, login, register, logout],
  )
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export const useAuth = () => useContext(AuthContext)
