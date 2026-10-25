interface Product {
  id: number;
  name: string;
  category: string;
  quantity: number;
}

interface DisplayProps {
  products: Product[];
}

function Display({ products }: DisplayProps) {
  return (
    <div>
      <h2>Product Details</h2>

      <table border={1} cellPadding={10}>
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Category</th>
            <th>Quantity</th>
          </tr>
        </thead>
        <tbody>
          {products.map((product) => (
            <tr key={product.id}>
              <td>{product.id}</td>
              <td>{product.name}</td>
              <td>{product.category}</td>
              <td>{product.quantity}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default Display;
