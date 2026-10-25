import { useImperativeHandle, useRef, type Ref } from "react";

type FieldHandle = { focus: () => void; clear: () => void };

function Field({ ref }: { ref?: Ref<FieldHandle> }) {
  const inputRef = useRef<HTMLInputElement>(null);

  useImperativeHandle(ref, () => ({
    focus() { inputRef.current?.focus(); },
    clear() { if (inputRef.current) inputRef.current.value = ""; }
  }), []);

  return <input ref={inputRef} />;
}

export default function UseImperativeHandle() {
  const fieldRef = useRef<FieldHandle>(null);

  return (
    <div>
      <Field ref={fieldRef} />
      <button onClick={() => fieldRef.current?.focus()}>Focus</button>
      <button onClick={() => fieldRef.current?.clear()}>Clear</button>
    </div>
  );
}
