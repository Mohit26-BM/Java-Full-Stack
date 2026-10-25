import './ProductCatalog.css'

function ProductCard({ name, category, price, rating, available, image }) {
  return (
    <article className="product-card">
      <img className="product-card__image" src={image} alt={name} />
      <div className="product-card__body">
        <h3 className="product-card__name">{name}</h3>
        <p className="product-card__category">{category}</p>
        <p className="product-card__price">Price: ₹{price.toLocaleString('en-IN')}</p>
        <p className="product-card__rating">Rating: {rating} / 5</p>
        <p className={available ? 'product-card__stock product-card__stock--in' : 'product-card__stock product-card__stock--out'}>
          {available ? 'Available' : 'Out of Stock'}
        </p>
      </div>
    </article>
  )
}

export default ProductCard
