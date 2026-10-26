import DiscountCodeForm from './DiscountCodeForm.jsx'
import { TAX_RATE, priceCart, rupees } from './pricing.js'

function CartPanel({ cart, code, onApplyCode, onChangeQuantity, onRemove }) {
  const price = priceCart(cart, code)

  return (
    <aside className="card" aria-label="Cart">
      <h2 className="section-title cart-layout__title">Cart ({price.itemCount} {price.itemCount === 1 ? 'item' : 'items'})</h2>
      {cart.length === 0 ? (
        <p className="muted">Your cart is empty.</p>
      ) : (
        <>
          <ul className="cart-lines">
            {cart.map((item) => (
              <li key={item.id} className="cart-lines__item">
                <span>{item.name}</span>
                <span className="cart-lines__qty">
                  <button type="button" className="button" onClick={() => onChangeQuantity(item.id, -1)}
                    aria-label={`Decrease ${item.name}`}>−</button>
                  <span aria-label={`Quantity of ${item.name}`}>{item.quantity}</span>
                  <button type="button" className="button" onClick={() => onChangeQuantity(item.id, 1)}
                    aria-label={`Increase ${item.name}`}>+</button>
                </span>
                <span>{rupees(item.price * item.quantity)}</span>
                <button type="button" className="button button--danger" onClick={() => onRemove(item.id)}
                  aria-label={`Remove ${item.name}`}>✕</button>
              </li>
            ))}
          </ul>
          <DiscountCodeForm applied={code} error={price.codeError} onApply={onApplyCode} />
          <dl className="totals">
            <dt>Subtotal</dt><dd>{rupees(price.subtotal)}</dd>
            {price.discount > 0 && <><dt>Discount ({code})</dt><dd>− {rupees(price.discount)}</dd></>}
            <dt>Tax ({TAX_RATE * 100}%)</dt><dd>{rupees(price.tax)}</dd>
            <dt className="totals__grand">Grand total</dt><dd className="totals__grand" data-testid="grand-total">{rupees(price.total)}</dd>
          </dl>
        </>
      )}
    </aside>
  )
}

export default CartPanel
