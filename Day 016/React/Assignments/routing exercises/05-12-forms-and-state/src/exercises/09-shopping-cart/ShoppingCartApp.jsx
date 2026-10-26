import { useState } from 'react'
import CartPanel from './CartPanel.jsx'
import { PRODUCTS, rupees } from './pricing.js'
import './ShoppingCart.css'

function ShoppingCartApp() {
  const [cart, setCart] = useState([])
  const [code, setCode] = useState('')

  const add = (product) => setCart((current) =>
    current.some((item) => item.id === product.id)
      ? current.map((item) => (item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item))
      : [...current, { ...product, quantity: 1 }])

  const changeQuantity = (id, delta) => setCart((current) => current
    .map((item) => (item.id === id ? { ...item, quantity: item.quantity + delta } : item))
    .filter((item) => item.quantity > 0))

  const remove = (id) => setCart((current) => current.filter((item) => item.id !== id))

  return (
    <section>
      <h1 className="page-title">Shopping Cart</h1>
      <div className="cart-layout">
        <div className="card">
          <h2 className="section-title cart-layout__title">Products</h2>
          <ul className="product-list">
            {PRODUCTS.map((product) => (
              <li key={product.id} className="product-list__item">
                <span>{product.name}</span>
                <strong>{rupees(product.price)}</strong>
                <button type="button" className="button button--primary" onClick={() => add(product)}
                  aria-label={`Add ${product.name}`}>Add</button>
              </li>
            ))}
          </ul>
        </div>
        <CartPanel cart={cart} code={code} onApplyCode={setCode} onChangeQuantity={changeQuantity} onRemove={remove} />
      </div>
    </section>
  )
}

export default ShoppingCartApp
