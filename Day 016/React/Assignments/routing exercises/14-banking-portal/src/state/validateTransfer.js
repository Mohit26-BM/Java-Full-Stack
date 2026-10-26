import { DAILY_TRANSFER_LIMIT } from './bankReducer.js'

export const REMARK_LIMIT = 50

export function validateTransfer(form, accounts) {
  const errors = {}
  const from = accounts.find((a) => a.id === form.fromAccountId)
  if (!from) errors.fromAccountId = 'Select the account to pay from'

  if (!form.toId) errors.toId = 'Select who to pay'
  else if (form.toId === form.fromAccountId) errors.toId = 'Choose a different account to pay into'

  const amount = Number(form.amount)
  if (form.amount === '') errors.amount = 'Amount is required'
  else if (!Number.isFinite(amount) || amount <= 0) errors.amount = 'Amount must be greater than 0'
  else if (Math.round(amount * 100) !== amount * 100) errors.amount = 'Amount can have at most 2 decimal places'
  else if (amount > DAILY_TRANSFER_LIMIT) errors.amount = `The daily limit is ₹${DAILY_TRANSFER_LIMIT.toLocaleString('en-IN')}`
  else if (from && amount > from.balance) errors.amount = `Insufficient balance: ₹${from.balance.toLocaleString('en-IN')} available`

  if (form.remark.length > REMARK_LIMIT) errors.remark = `Remark can be at most ${REMARK_LIMIT} characters`
  return errors
}
