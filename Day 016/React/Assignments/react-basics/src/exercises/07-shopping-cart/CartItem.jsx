import { formatRupees } from './cartMath.js'

function CartItem({ item, onChangeQuantity, onRemove }) {
  return (
    <li className="shop__cart-item">
      <div className="shop__cart-info">
        <span className="shop__product-name">{item.name}</span>
        <span className="shop__line-total">{formatRupees(item.price * item.quantity)}</span>
      </div>
      <div className="shop__quantity">
        <button type="button" className="shop__button" onClick={() => onChangeQuantity(item.id, -1)}
          aria-label={`Decrease ${item.name}`}>−</button>
        <span aria-label={`Quantity of ${item.name}`}>{item.quantity}</span>
        <button type="button" className="shop__button" onClick={() => onChangeQuantity(item.id, 1)}
          aria-label={`Increase ${item.name}`}>+</button>
        <button type="button" className="shop__button shop__button--danger" onClick={() => onRemove(item.id)}
          aria-label={`Remove ${item.name}`}>Remove</button>
      </div>
    </li>
  )
}

export default CartItem
