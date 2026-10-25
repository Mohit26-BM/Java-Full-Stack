import { useId } from "react";

export default function UseId() {
  const id = useId();

  return (
    <div>
      <label htmlFor={id}>Email address</label>
      <input id={id} type="email" />
    </div>
  );
}
