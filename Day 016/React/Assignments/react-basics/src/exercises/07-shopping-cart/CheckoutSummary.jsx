import { DISCOUNT_THRESHOLD, TAX_RATE, formatRupees } from './cartMath.js'

function CheckoutSummary({ summary }) {
  return (
    <dl className="shop__summary" aria-label="Checkout summary">
      <dt>Subtotal</dt>
      <dd>{formatRupees(summary.subtotal)}</dd>
      <dt>Discount (10% above {formatRupees(DISCOUNT_THRESHOLD)})</dt>
      <dd>{summary.discount > 0 ? `− ${formatRupees(summary.discount)}` : formatRupees(0)}</dd>
      <dt>GST ({TAX_RATE * 100}%)</dt>
      <dd>{formatRupees(summary.tax)}</dd>
      <dt className="shop__summary-total">Total</dt>
      <dd className="shop__summary-total" data-testid="cart-total">{formatRupees(summary.total)}</dd>
    </dl>
  )
}

export default CheckoutSummary
