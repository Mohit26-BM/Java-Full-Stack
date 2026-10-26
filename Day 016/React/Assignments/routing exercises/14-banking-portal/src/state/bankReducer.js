export const DEMO_LOGIN = { username: 'priya', password: 'bank@123' }
export const DAILY_TRANSFER_LIMIT = 100000

export const PAYEES = [
  { id: 'PAY-1', name: 'Ravi Iyer', bank: 'HDFC Bank', accountNumber: 'XXXX4471' },
  { id: 'PAY-2', name: 'Shah Electricals', bank: 'ICICI Bank', accountNumber: 'XXXX9023' },
]

export const initialBankState = {
  user: null,
  accounts: [
    { id: 'SB-1001', type: 'Savings', name: 'Everyday Savings', balance: 84500 },
    { id: 'SB-1002', type: 'Savings', name: 'Emergency Fund', balance: 150000 },
    { id: 'CA-2001', type: 'Current', name: 'Business Current', balance: 42000 },
  ],
  transactions: [
    { id: 'TXN-1003', date: '2026-10-24T09:15:00.000Z', kind: 'DEBIT', accountId: 'SB-1001', amount: 1200, description: 'Electricity bill' },
    { id: 'TXN-1002', date: '2026-10-22T13:40:00.000Z', kind: 'CREDIT', accountId: 'SB-1001', amount: 65000, description: 'Salary October' },
    { id: 'TXN-1001', date: '2026-10-20T08:05:00.000Z', kind: 'CREDIT', accountId: 'CA-2001', amount: 18000, description: 'Invoice #431 payment' },
  ],
  pendingTransfer: null,
}

const nextTransactionNumber = (transactions) =>
  Math.max(1000, ...transactions.map((t) => Number(t.id.split('-')[1]))) + 1

export function bankReducer(state, action) {
  switch (action.type) {
    case 'LOGIN':
      return { ...state, user: action.user }

    case 'LOGOUT':
      return { ...state, user: null, pendingTransfer: null }

    case 'PREPARE_TRANSFER':
      return { ...state, pendingTransfer: action.transfer }

    case 'CANCEL_TRANSFER':
      return { ...state, pendingTransfer: null }

    case 'COMPLETE_TRANSFER': {
      const transfer = state.pendingTransfer
      const from = transfer && state.accounts.find((a) => a.id === transfer.fromAccountId)
      if (!from || transfer.amount > from.balance) return state
      const toOwn = state.accounts.find((a) => a.id === transfer.toId)
      const number = nextTransactionNumber(state.transactions)
      const debit = {
        id: `TXN-${number}`,
        date: action.timestamp,
        kind: 'DEBIT',
        accountId: from.id,
        amount: transfer.amount,
        description: `Transfer to ${transfer.toLabel}${transfer.remark ? ` — ${transfer.remark}` : ''}`,
        transferTo: transfer.toLabel,
      }
      const credit = toOwn && {
        id: `TXN-${number + 1}`,
        date: action.timestamp,
        kind: 'CREDIT',
        accountId: toOwn.id,
        amount: transfer.amount,
        description: `Transfer from ${from.name} (${from.id})`,
      }
      return {
        ...state,
        accounts: state.accounts.map((a) => {
          if (a.id === from.id) return { ...a, balance: a.balance - transfer.amount }
          if (toOwn && a.id === toOwn.id) return { ...a, balance: a.balance + transfer.amount }
          return a
        }),
        transactions: [...(credit ? [credit] : []), debit, ...state.transactions],
        pendingTransfer: null,
        lastTransferId: debit.id,
      }
    }

    default:
      return state
  }
}
