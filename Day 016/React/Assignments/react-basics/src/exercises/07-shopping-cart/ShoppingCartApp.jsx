import { useState } from 'react'
import Cart from './Cart.jsx'
import ProductList from './ProductList.jsx'
import { catalog } from './catalog.js'
import './ShoppingCart.css'

function ShoppingCartApp() {
  const [cart, setCart] = useState([])

  const addToCart = (product) => {
    setCart((current) =>
      current.some((item) => item.id === product.id)
        ? current.map((item) => (item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item))
        : [...current, { ...product, quantity: 1 }])
  }

  const changeQuantity = (id, delta) => {
    setCart((current) => current
      .map((item) => (item.id === id ? { ...item, quantity: item.quantity + delta } : item))
      .filter((item) => item.quantity > 0))
  }

  const removeFromCart = (id) => {
    setCart((current) => current.filter((item) => item.id !== id))
  }

  return (
    <section>
      <h1 className="page-title">Shopping Cart</h1>
      <div className="shop">
        <ProductList products={catalog} onAdd={addToCart} />
        <Cart cart={cart} onChangeQuantity={changeQuantity} onRemove={removeFromCart} />
      </div>
    </section>
  )
}

export default ShoppingCartApp
