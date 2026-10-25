// Replaced by ProductValidate.tsx - kept here commented out for reference.
export {};

// import { useState } from "react";
// import { type FormEvent } from "react";
// function Register(){
//     const [id, setId] = useState("");
//   const [name, setName] = useState("");
//   const [category, setCategory] = useState("");
//   const [quantity, setQuantity] = useState("");
//   const [price, setPrice] = useState("");
//   const [message, setMessage] = useState("");
//   const [idmsg, setIdmsg] = useState("");
//   const [catmsg, setCatmsg] = useState("");
//   const [qtymsg, setQtymsg] = useState("");
//    const [pricemsg, setPricemsg] = useState("");
//
//   function handleSubmit(event: FormEvent<HTMLFormElement>) {
//     event.preventDefault();
//
//     // Step 1: Check whether any field is empty.
//     if (
//       id.trim() === "" ||
//       name.trim() === "" ||
//       category === "" ||
//       quantity.trim() === "" ||
//       price.trim() === ""
//     ) {
//       setMessage("Please fill in all fields.");
//       return;
//     }
//
//     // Step 2: Validate the product ID.
//     if (
//       !Number.isInteger(Number(id)) ||
//       Number(id) < 1 ||
//       Number(id) > 100
//     ) {
//       setIdmsg("ID must be a whole number between 1 and 100.");
//       return;
//     }
//
//     // Step 3: Validate the category.
//     if (
//       category !== "Electronics" &&
//       category !== "Furniture" &&
//       category !== "Stationary"
//     ) {
//       setCatmsg("Please select a valid category.");
//       return;
//     }
//
//     // Step 4: Validate the quantity.
//     if (
//       !Number.isInteger(Number(quantity)) ||
//       Number(quantity) <= 0
//     ) {
//       setQtymsg("Quantity must be a positive whole number.");
//       return;
//     }
//
//     // Step 5: Validate the price.
//     if (
//       !Number.isFinite(Number(price)) ||
//       Number(price) < 0
//     ) {
//       setPricemsg("Price must be a valid, non-negative number.");
//       return;
//     }
//
//     // Step 6: All validations passed.
//     setMessage("Product details are valid!");
//
//     console.log({
//       id: Number(id),
//       name: name.trim(),
//       category: category,
//       quantity: Number(quantity),
//       price: Number(price)
//     });
//   }
//
//   return (
//     <div>
//       <h1>Product Form</h1>
//
//       <form onSubmit={handleSubmit} noValidate>
//         <p>
//           <label htmlFor="id">Product ID: </label>
//           <input
//             id="id"
//             type="number"
//             value={id}
//             onChange={(event) => setId(event.target.value)}
//           /> {idmsg}
//         </p>
//
//         <p>
//           <label htmlFor="name">Product Name: </label>
//           <input
//             id="name"
//             type="text"
//             value={name}
//             onChange={(event) => setName(event.target.value)}
//           />
//         </p>
//
//         <p>
//           <label htmlFor="category">Category: </label>
//           <select
//             id="category"
//             value={category}
//             onChange={(event) => setCategory(event.target.value)}
//           >
//             <option value="">Select Category</option>
//             <option value="Electronics">Electronics</option>
//             <option value="Furniture">Furniture</option>
//             <option value="Stationary">Stationary</option>
//           </select>
//         </p>{catmsg}
//
//         <p>
//           <label htmlFor="quantity">Quantity: </label>
//           <input
//             id="quantity"
//             type="number"
//             value={quantity}
//             onChange={(event) => setQuantity(event.target.value)}
//           />
//         </p>{qtymsg}
//
//         <p>
//           <label htmlFor="price">Price: </label>
//           <input
//             id="price"
//             type="number"
//             value={price}
//             onChange={(event) => setPrice(event.target.value)}
//           />
//         </p>{pricemsg}
//
//         <button type="submit">Submit Product</button>
//       </form>
//
//       <p>{message}</p>
//
//
//
//     </div>
//   );
// }
//
// export default Register;
