import ProductList from './ProductList.jsx'
import { products } from './products.js'

function ProductCatalogApp() {
  return (
    <section>
      <h1 className="page-title">Product Catalog</h1>
      <ProductList products={products} />
    </section>
  )
}

export default ProductCatalogApp
