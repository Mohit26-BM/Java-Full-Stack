import { useLayoutEffect, useRef, useState } from "react";

export default function UseLayoutEffect() {
  const boxRef = useRef<HTMLDivElement>(null);
  const [height, setHeight] = useState(0);

  useLayoutEffect(() => {
    const element = boxRef.current;
    if (element) setHeight(element.getBoundingClientRect().height);
  }, []);

  return (
    <div>
      <div ref={boxRef} style={{ padding: 20, border: "1px solid black" }}>
        Measure this box
      </div>
      <p>Height: {height}px</p>
    </div>
  );
}
