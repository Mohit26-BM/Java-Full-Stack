import { email, firstError, mobile, onlyErrors, positiveNumber, required } from '../../shared/validators.js'

export const EMPTY_APPLICATION = {
  fullName: '', email: '', mobile: '', age: '',
  employmentType: '', employerName: '', monthlyIncome: '', yearsEmployed: '',
  loanType: '', amount: '', tenureMonths: '', purpose: '',
}

export const LOAN_LIMITS = { minAmount: 10000, maxAmount: 5000000, minTenure: 6, maxTenure: 360, minAge: 21, maxAge: 65 }

const between = (value, min, max, message) => {
  const n = Number(value)
  return Number.isFinite(n) && n >= min && n <= max ? '' : message
}

export const STEPS = [
  {
    title: 'Personal information',
    fields: [
      { name: 'fullName', label: 'Full name' },
      { name: 'email', label: 'Email', type: 'email' },
      { name: 'mobile', label: 'Mobile number', type: 'tel' },
      { name: 'age', label: 'Age', type: 'number' },
    ],
    validate: (a) => onlyErrors({
      fullName: required(a.fullName, 'Full name'),
      email: firstError(required(a.email, 'Email'), email(a.email)),
      mobile: firstError(required(a.mobile, 'Mobile number'), mobile(a.mobile)),
      age: firstError(required(a.age, 'Age'),
        between(a.age, LOAN_LIMITS.minAge, LOAN_LIMITS.maxAge, `Applicants must be ${LOAN_LIMITS.minAge} to ${LOAN_LIMITS.maxAge} years old`)),
    }),
  },
  {
    title: 'Employment information',
    fields: [
      { name: 'employmentType', label: 'Employment type', options: ['Salaried', 'Self-employed'] },
      { name: 'employerName', label: 'Employer / business name' },
      { name: 'monthlyIncome', label: 'Monthly income (₹)', type: 'number' },
      { name: 'yearsEmployed', label: 'Years in current job', type: 'number' },
    ],
    validate: (a) => onlyErrors({
      employmentType: required(a.employmentType, 'Employment type'),
      employerName: required(a.employerName, 'Employer / business name'),
      monthlyIncome: firstError(required(a.monthlyIncome, 'Monthly income'), positiveNumber(a.monthlyIncome, 'Monthly income')),
      yearsEmployed: firstError(required(a.yearsEmployed, 'Years in current job'),
        between(a.yearsEmployed, 0, 50, 'Years must be between 0 and 50')),
    }),
  },
  {
    title: 'Loan information',
    fields: [
      { name: 'loanType', label: 'Loan type', options: ['Home', 'Car', 'Personal', 'Education'] },
      { name: 'amount', label: 'Loan amount (₹)', type: 'number' },
      { name: 'tenureMonths', label: 'Tenure (months)', type: 'number' },
      { name: 'purpose', label: 'Purpose' },
    ],
    validate: (a) => onlyErrors({
      loanType: required(a.loanType, 'Loan type'),
      amount: firstError(required(a.amount, 'Loan amount'),
        between(a.amount, LOAN_LIMITS.minAmount, LOAN_LIMITS.maxAmount, 'Loan amount must be between ₹10,000 and ₹50,00,000')),
      tenureMonths: firstError(required(a.tenureMonths, 'Tenure'),
        between(a.tenureMonths, LOAN_LIMITS.minTenure, LOAN_LIMITS.maxTenure, 'Tenure must be between 6 and 360 months')),
      purpose: required(a.purpose, 'Purpose'),
    }),
  },
]

export const TOTAL_STEPS = STEPS.length + 1
