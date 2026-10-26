import { firstError, onlyErrors, positiveNumber, required } from '../../shared/validators.js'

export const ACCOUNT_TYPES = ['Savings', 'Current']
export const MIN_DEPOSIT = { Savings: 1000, Current: 5000 }
export const MIN_BALANCE_OPTIONS = ['Zero balance', '₹1,000 average monthly balance', '₹5,000 average monthly balance']
export const RELATIONSHIPS = ['Spouse', 'Parent', 'Child', 'Sibling', 'Other']

let nextNomineeId = 1
export const newNominee = () => ({ id: nextNomineeId++, name: '', relationship: '' })

export const initialForm = () => ({
  customerName: '',
  accountType: '',
  initialDeposit: '',
  minimumBalance: '',
  businessName: '',
  nominees: [newNominee()],
})

export function toSubmission(form) {
  const base = {
    customerName: form.customerName.trim(),
    accountType: form.accountType,
    initialDeposit: Number(form.initialDeposit) || 0,
  }
  const specific = form.accountType === 'Savings' ? { minimumBalance: form.minimumBalance }
    : form.accountType === 'Current' ? { businessName: form.businessName.trim() }
      : {}
  const nominees = form.nominees.map(({ name, relationship }) => ({ name: name.trim(), relationship }))
  return { ...base, ...specific, nominees }
}

export function validateBankForm(form) {
  const errors = {
    customerName: required(form.customerName, 'Customer name'),
    accountType: required(form.accountType, 'Account type'),
    initialDeposit: firstError(required(form.initialDeposit, 'Initial deposit'), positiveNumber(form.initialDeposit, 'Initial deposit')),
  }
  const min = MIN_DEPOSIT[form.accountType]
  if (!errors.initialDeposit && min && Number(form.initialDeposit) < min) {
    errors.initialDeposit = `A ${form.accountType} account needs at least ₹${min.toLocaleString('en-IN')}`
  }
  if (form.accountType === 'Savings') errors.minimumBalance = required(form.minimumBalance, 'Minimum balance preference')
  if (form.accountType === 'Current') errors.businessName = required(form.businessName, 'Business name')
  if (form.nominees.length === 0) errors.nominees = 'Add at least one nominee'

  form.nominees.forEach((nominee, index) => {
    errors[`nominee-${nominee.id}-name`] = required(nominee.name, `Nominee ${index + 1} name`)
    errors[`nominee-${nominee.id}-relationship`] = required(nominee.relationship, `Nominee ${index + 1} relationship`)
  })
  return onlyErrors(errors)
}
