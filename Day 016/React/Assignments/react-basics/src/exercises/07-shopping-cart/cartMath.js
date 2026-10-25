export const DISCOUNT_THRESHOLD = 5000
export const DISCOUNT_RATE = 0.1
export const TAX_RATE = 0.18

const round2 = (value) => Math.round(value * 100) / 100

export function summarize(cart) {
  const itemCount = cart.reduce((count, item) => count + item.quantity, 0)
  const subtotal = cart.reduce((sum, item) => sum + item.price * item.quantity, 0)
  const discount = subtotal > DISCOUNT_THRESHOLD ? round2(subtotal * DISCOUNT_RATE) : 0
  const tax = round2((subtotal - discount) * TAX_RATE)
  const total = round2(subtotal - discount + tax)
  return { itemCount, subtotal, discount, tax, total }
}

export const formatRupees = (value) =>
  `₹${value.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
