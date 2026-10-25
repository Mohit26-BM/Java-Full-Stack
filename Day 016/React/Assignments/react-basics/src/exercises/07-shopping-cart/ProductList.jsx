import { formatRupees } from './cartMath.js'

function ProductList({ products, onAdd }) {
  return (
    <div className="shop__products">
      <h2 className="shop__heading">Products</h2>
      <ul className="shop__product-list">
        {products.map((product) => (
          <li key={product.id} className="shop__product">
            <span className="shop__product-name">{product.name}</span>
            <span className="shop__product-price">{formatRupees(product.price)}</span>
            <button type="button" className="shop__button shop__button--primary" onClick={() => onAdd(product)}
              aria-label={`Add ${product.name}`}>
              Add
            </button>
          </li>
        ))}
      </ul>
    </div>
  )
}

export default ProductList
