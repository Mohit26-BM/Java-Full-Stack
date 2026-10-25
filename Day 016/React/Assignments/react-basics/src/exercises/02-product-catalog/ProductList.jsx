import ProductCard from './ProductCard.jsx'

function ProductList({ products }) {
  return (
    <div className="product-list">
      {products.map((product) => (
        <ProductCard
          key={product.id}
          name={product.name}
          category={product.category}
          price={product.price}
          rating={product.rating}
          available={product.available}
          image={product.image}
        />
      ))}
    </div>
  )
}

export default ProductList
