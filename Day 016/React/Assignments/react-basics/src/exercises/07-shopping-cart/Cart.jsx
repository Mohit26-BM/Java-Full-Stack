import CartItem from './CartItem.jsx'
import CheckoutSummary from './CheckoutSummary.jsx'
import { summarize } from './cartMath.js'

function Cart({ cart, onChangeQuantity, onRemove }) {
  const summary = summarize(cart)

  return (
    <aside className="shop__cart" aria-label="Cart">
      <h2 className="shop__heading">Cart ({summary.itemCount} {summary.itemCount === 1 ? 'item' : 'items'})</h2>
      {cart.length === 0 ? (
        <p className="shop__empty">Your cart is empty. Add a product to get started.</p>
      ) : (
        <>
          <ul className="shop__cart-list">
            {cart.map((item) => (
              <CartItem key={item.id} item={item} onChangeQuantity={onChangeQuantity} onRemove={onRemove} />
            ))}
          </ul>
          <CheckoutSummary summary={summary} />
        </>
      )}
    </aside>
  )
}

export default Cart
