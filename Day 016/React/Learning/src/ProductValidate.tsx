import "bootstrap/dist/css/bootstrap.min.css";
import { useState, type ChangeEvent, type FormEvent } from "react";

type ProductForm = {
  id: string;
  name: string;
  category: string;
  quantity: string;
  price: string;
};

type FieldName = keyof ProductForm;
type FormErrors = Record<FieldName, string>;

type Product = {
  id: number;
  name: string;
  category: string;
  quantity: number;
  price: number;
};

const initialForm: ProductForm = {
  id: "",
  name: "",
  category: "",
  quantity: "",
  price: "",
};

const initialErrors: FormErrors = {
  id: "",
  name: "",
  category: "",
  quantity: "",
  price: "",
};

// Return an empty string when the field is valid,
// or an error message when the field is invalid.
function validateField(field: FieldName, value: string): string {
  switch (field) {
    case "id":
      return value.trim() === ""
        ? "ID is required."
        : !Number.isFinite(Number(value))
          ? "Enter a valid ID."
          : !Number.isInteger(Number(value))
            ? "ID must be a whole number."
            : Number(value) < 1
              ? "ID must be at least 1."
              : Number(value) > 100
                ? "ID cannot be greater than 100."
                : "";

    case "name":
      return value.trim() === "" ? "Name is required." : "";

    case "category":
      return value === ""
        ? "Please select a category."
        : ["Electronics", "Furniture", "Stationary"].includes(value)
          ? ""
          : "Choose an allowed category.";

    case "quantity":
      return value.trim() === ""
        ? "Quantity is required."
        : !Number.isFinite(Number(value))
          ? "Enter a valid quantity."
          : !Number.isInteger(Number(value))
            ? "Quantity must be a whole number."
            : Number(value) <= 0
              ? "Quantity must be greater than 0."
              : "";

    case "price":
      return value.trim() === ""
        ? "Price is required."
        : !Number.isFinite(Number(value))
          ? "Enter a valid price."
          : Number(value) < 0
            ? "Price cannot be negative."
            : "";
  }
  return "";
}

function ProductValidate() {
  const [form, setForm] = useState<ProductForm>(initialForm);
  const [errors, setErrors] = useState<FormErrors>(initialErrors);
  const [submittedProduct, setSubmittedProduct] = useState<Product | null>(
    null,
  );
  const [successMessage, setSuccessMessage] = useState(""); // Runs whenever the user types or changes a field.

  function handleChange(
    event: ChangeEvent<HTMLInputElement | HTMLSelectElement>,
  ) {
    const { name, value } = event.target;
    const field = name as FieldName;

    setForm((currentForm) => ({
      ...currentForm,
      [field]: value,
    })); // Immediately validate the field that changed.

    setErrors((currentErrors) => ({
      ...currentErrors,
      [field]: validateField(field, value),
    }));

    setSubmittedProduct(null);
    setSuccessMessage("");
  } // Check every field again when Submit is clicked.

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const nextErrors: FormErrors = {
      id: validateField("id", form.id),
      name: validateField("name", form.name),
      category: validateField("category", form.category),
      quantity: validateField("quantity", form.quantity),
      price: validateField("price", form.price),
    };

    setErrors(nextErrors);

    const isValid = Object.values(nextErrors).every((error) => error === "");

    const product: Product = {
      id: Number(form.id),
      name: form.name.trim(),
      category: form.category,
      quantity: Number(form.quantity),
      price: Number(form.price),
    };

    setSubmittedProduct(isValid ? product : null);
    setSuccessMessage(isValid ? "Product submitted successfully!" : "");
  }

  function handleReset() {
    setForm(initialForm);
    setErrors(initialErrors);
    setSubmittedProduct(null);
    setSuccessMessage("");
  }

  return (
    <main className="container my-4" style={{ maxWidth: 560 }}>
      <div className="card">
        <div className="card-header">
          <h1 className="text-2xl mb-0">Product Details</h1>
        </div>
        <div className="card-body">
          <form onSubmit={handleSubmit} noValidate>
        <div className="mb-3">
          <label htmlFor="id" className="form-label">Product ID</label>
          <input
            id="id"
            name="id"
            type="number"
            min="1"
            max="100"
            step="1"
            className={`form-control ${errors.id ? "is-invalid" : ""}`}
            value={form.id}
            onChange={handleChange}
            aria-invalid={Boolean(errors.id)}
            aria-describedby={errors.id ? "id-error" : undefined}
          />
          {errors.id && (
            <div id="id-error" className="invalid-feedback" role="alert">
              {errors.id}
            </div>
          )}
        </div>
        <div className="mb-3">
          <label htmlFor="name" className="form-label">Product Name</label>
          <input
            id="name"
            name="name"
            type="text"
            className={`form-control ${errors.name ? "is-invalid" : ""}`}
            value={form.name}
            onChange={handleChange}
            aria-invalid={Boolean(errors.name)}
            aria-describedby={errors.name ? "name-error" : undefined}
          />
          {errors.name && (
            <div id="name-error" className="invalid-feedback" role="alert">
              {errors.name}
            </div>
          )}
        </div>
        <div className="mb-3">
          <label htmlFor="category" className="form-label">Category</label>
          <select
            id="category"
            name="category"
            className={`form-control ${errors.category ? "is-invalid" : ""}`}
            value={form.category}
            onChange={handleChange}
            aria-invalid={Boolean(errors.category)}
            aria-describedby={errors.category ? "category-error" : undefined}
          >
            <option value="">-- Select a category --</option>
            <option value="Electronics">Electronics</option>
            <option value="Furniture">Furniture</option>
            <option value="Stationary">Stationary</option>
          </select>
          {errors.category && (
            <div id="category-error" className="invalid-feedback" role="alert">
              {errors.category}
            </div>
          )}
        </div>
        <div className="mb-3">
          <label htmlFor="quantity" className="form-label">Quantity</label>
          <input
            id="quantity"
            name="quantity"
            type="number"
            min="1"
            step="1"
            className={`form-control ${errors.quantity ? "is-invalid" : ""}`}
            value={form.quantity}
            onChange={handleChange}
            aria-invalid={Boolean(errors.quantity)}
            aria-describedby={errors.quantity ? "quantity-error" : undefined}
          />
          {errors.quantity && (
            <div id="quantity-error" className="invalid-feedback" role="alert">
              {errors.quantity}
            </div>
          )}
        </div>
        <div className="mb-3">
          <label htmlFor="price" className="form-label">Price</label>
          <input
            id="price"
            name="price"
            type="number"
            min="0"
            step="any"
            className={`form-control ${errors.price ? "is-invalid" : ""}`}
            value={form.price}
            onChange={handleChange}
            aria-invalid={Boolean(errors.price)}
            aria-describedby={errors.price ? "price-error" : undefined}
          />
          {errors.price && (
            <div id="price-error" className="invalid-feedback" role="alert">
              {errors.price}
            </div>
          )}
        </div>

            <button type="submit" className="btn btn-solid theme-primary">
              Submit Product
            </button>{" "}
            <button
              type="button"
              className="btn btn-outline theme-secondary"
              onClick={handleReset}
            >
              Reset
            </button>
          </form>

          {successMessage && (
            <div className="alert theme-success mt-3" role="status">
              {successMessage}
            </div>
          )}

          {submittedProduct && (
            <section className="card mt-3">
              <div className="card-header">Submitted Product</div>
              <div className="card-body">
                <p className="mb-1">ID: {submittedProduct.id}</p>
                <p className="mb-1">Name: {submittedProduct.name}</p>
                <p className="mb-1">Category: {submittedProduct.category}</p>
                <p className="mb-1">Quantity: {submittedProduct.quantity}</p>
                <p className="mb-0">Price: {submittedProduct.price}</p>
              </div>
            </section>
          )}
        </div>
      </div>
    </main>
  );
}

export default ProductValidate;
