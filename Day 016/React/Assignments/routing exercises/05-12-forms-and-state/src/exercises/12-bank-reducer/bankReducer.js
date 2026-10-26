export const ACCOUNTS = { savings: 'Savings account', current: 'Current account' }

export const initialBankState = {
  balances: { savings: 10000, current: 5000 },
  transactions: [],
  error: '',
}

const reject = (state, error) => ({ ...state, error })

const record = (state, balances, transaction) => ({
  balances,
  transactions: [{ id: state.transactions.length + 1, ...transaction }, ...state.transactions],
  error: '',
})

export function bankReducer(state, action) {
  const { amount, timestamp } = action
  const isValidAmount = typeof amount === 'number' && Number.isFinite(amount) && amount > 0

  switch (action.type) {
    case 'DEPOSIT': {
      if (!isValidAmount) return reject(state, 'Amount must be greater than 0')
      const { account } = action
      return record(state, { ...state.balances, [account]: state.balances[account] + amount },
        { type: 'DEPOSIT', amount, account, timestamp })
    }
    case 'WITHDRAW': {
      if (!isValidAmount) return reject(state, 'Amount must be greater than 0')
      const { account } = action
      if (amount > state.balances[account]) {
        return reject(state, `Insufficient funds: ${ACCOUNTS[account]} has ₹${state.balances[account].toLocaleString('en-IN')}`)
      }
      return record(state, { ...state.balances, [account]: state.balances[account] - amount },
        { type: 'WITHDRAW', amount, account, timestamp })
    }
    case 'TRANSFER': {
      if (!isValidAmount) return reject(state, 'Amount must be greater than 0')
      const { from, to } = action
      if (from === to) return reject(state, 'Choose two different accounts')
      if (amount > state.balances[from]) {
        return reject(state, `Insufficient funds: ${ACCOUNTS[from]} has ₹${state.balances[from].toLocaleString('en-IN')}`)
      }
      return record(state, {
        ...state.balances,
        [from]: state.balances[from] - amount,
        [to]: state.balances[to] + amount,
      }, { type: 'TRANSFER', amount, from, to, timestamp })
    }
    case 'RESET':
      return initialBankState
    default:
      return state
  }
}
