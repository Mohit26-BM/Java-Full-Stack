import { useParams } from "react-router-dom";

export default function ProductDetails() {
    const { id,name } = useParams();

    return (
      <section>
        <h2>Product Details</h2>
        <p>Product ID: {id}</p>
        <p> Product Name: {name}</p>
      </section>
    );
  }
