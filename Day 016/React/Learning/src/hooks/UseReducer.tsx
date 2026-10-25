import { useReducer } from "react";

type State = { quantity: number };
type Action = { type: "add" } | { type: "remove" } | { type: "reset" };

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case "add":
      return { quantity: state.quantity + 1 };
    case "remove":
      return { quantity: Math.max(0, state.quantity - 1) };
    case "reset":
      return { quantity: 0 };
    default:
      return state;
  }
}

export default function UseReducer() {
  const [state, dispatch] = useReducer(reducer, { quantity: 0 });

  return (
    <div>
      <p>Cart quantity: {state.quantity}</p>
      <button onClick={() => dispatch({ type: "add" })}>Add</button>
      <button onClick={() => dispatch({ type: "remove" })}>Remove</button>
      <button onClick={() => dispatch({ type: "reset" })}>Reset</button>
    </div>
  );
}
