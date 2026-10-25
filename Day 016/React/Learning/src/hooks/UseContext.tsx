import { createContext, useContext, useState, type ReactNode } from "react";

type Theme = "light" | "dark";

const ThemeContext = createContext<{
  theme: Theme;
  toggleTheme: () => void;
} | null>(null);

function ThemeProvider({ children }: { children: ReactNode }) {
  const [theme, setTheme] = useState<Theme>("light");

  function toggleTheme() {
    setTheme(current => (current === "light" ? "dark" : "light"));
  }

  return (
    <ThemeContext.Provider value={{ theme, toggleTheme }}>
      {children}
    </ThemeContext.Provider>
  );
}

function ThemePanel() {
  const context = useContext(ThemeContext);
  if (!context) throw new Error("ThemePanel must be used inside ThemeProvider");

  return (
    <div>
      <p>Current theme: {context.theme}</p>
      <button onClick={context.toggleTheme}>Toggle theme</button>
    </div>
  );
}

export default function UseContext() {
  return (
    <ThemeProvider>
      <ThemePanel />
    </ThemeProvider>
  );
}
