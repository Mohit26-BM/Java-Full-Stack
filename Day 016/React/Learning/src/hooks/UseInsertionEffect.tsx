import { useInsertionEffect } from "react";

export default function UseInsertionEffect() {
  // Meant for CSS-in-JS library authors: injects a style before layout effects run.
  useInsertionEffect(() => {
    const style = document.createElement("style");
    style.textContent = ".inserted { color: green; }";
    document.head.appendChild(style);
    return () => style.remove();
  }, []);

  return <p className="inserted">Styled content</p>;
}
