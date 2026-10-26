import { createContext, useContext, useReducer } from 'react'
import { DEMO_LOGIN, bankReducer, initialBankState } from './bankReducer.js'

const BankContext = createContext(null)

export function BankProvider({ children, initialState = initialBankState }) {
  const [state, dispatch] = useReducer(bankReducer, initialState)

  const actions = {
    login: (username, password) => {
      if (username !== DEMO_LOGIN.username || password !== DEMO_LOGIN.password) return false
      dispatch({
        type: 'LOGIN',
        user: { username, name: 'Priya Menon', email: 'priya.menon@example.com', phone: '9876501234', customerId: 'CUST-58210' },
      })
      return true
    },
    logout: () => dispatch({ type: 'LOGOUT' }),
    prepareTransfer: (transfer) => dispatch({ type: 'PREPARE_TRANSFER', transfer }),
    cancelTransfer: () => dispatch({ type: 'CANCEL_TRANSFER' }),
    completeTransfer: () => dispatch({ type: 'COMPLETE_TRANSFER', timestamp: new Date().toISOString() }),
  }

  const findAccount = (id) => state.accounts.find((a) => a.id === id)
  const findTransaction = (id) => state.transactions.find((t) => t.id === id)

  return (
    <BankContext.Provider value={{ ...state, ...actions, findAccount, findTransaction }}>
      {children}
    </BankContext.Provider>
  )
}

export function useBank() {
  const context = useContext(BankContext)
  if (!context) throw new Error('useBank must be used inside <BankProvider>')
  return context
}
