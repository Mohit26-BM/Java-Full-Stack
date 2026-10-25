import Display from "./Display";

interface Product {
  id: number;
  name: string;
  category: string;
  quantity: number;
}

function ProductComponent() {
  const products: Product[] = [
    {
      id: 101,
      name: "Laptop",
      category: "Electronics",
      quantity: 10,
    },
    {
      id: 102,
      name: "Mobile",
      category: "Electronics",
      quantity: 25,
    },
    {
      id: 103,
      name: "Chair",
      category: "Furniture",
      quantity: 15,
    },
    {
      id: 104,
      name: "Notebook",
      category: "Stationery",
      quantity: 50,
    },
  ];

  return (
    <div>
      <h2>Product Component</h2>

      <Display products={products} />
    </div>
  );
}

export default ProductComponent;
