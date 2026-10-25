const money = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' })

export const formatMoney = (value) => (value == null ? '-' : money.format(value))

export const formatDate = (value) =>
  value ? new Date(value).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' }) : '-'

export const titleCase = (value) => (value ? value.charAt(0) + value.slice(1).toLowerCase() : '')
