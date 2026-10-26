export const PRODUCTS = [
  { id: 1, name: 'Notebook Set', price: 299 },
  { id: 2, name: 'Desk Organizer', price: 649 },
  { id: 3, name: 'Wireless Mouse', price: 899 },
  { id: 4, name: 'Office Chair Cushion', price: 1299 },
  { id: 5, name: 'Mechanical Keyboard', price: 3499 },
  { id: 6, name: 'Monitor Arm', price: 2499 },
]

export const TAX_RATE = 0.18

export const DISCOUNT_CODES = {
  SAVE10: { description: '10% off', apply: (subtotal) => ({ discount: subtotal * 0.1 }) },
  FLAT500: {
    description: '₹500 off orders of ₹2,000 or more',
    apply: (subtotal) => (subtotal >= 2000 ? { discount: 500 } : { error: 'FLAT500 needs a subtotal of at least ₹2,000' }),
  },
}

const round2 = (n) => Math.round(n * 100) / 100

export function priceCart(cart, code) {
  const subtotal = cart.reduce((sum, item) => sum + item.price * item.quantity, 0)
  const rule = code ? DISCOUNT_CODES[code] : null
  const result = rule ? rule.apply(subtotal) : {}
  const discount = round2(Math.min(result.discount ?? 0, subtotal))
  const tax = round2((subtotal - discount) * TAX_RATE)
  return {
    itemCount: cart.reduce((n, item) => n + item.quantity, 0),
    subtotal,
    discount,
    tax,
    total: round2(subtotal - discount + tax),
    codeError: result.error ?? '',
  }
}

export const rupees = (n) => `₹${n.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
