import { useState } from "react";

export default function SelectDropdown() {
  const [city, setCity] = useState<string>("Hyderabad");

  return (
    <div>
      <select value={city} onChange={event => setCity(event.target.value)}>
        <option value="Hyderabad">Hyderabad</option>
        <option value="Bengaluru">Bengaluru</option>
        <option value="Chennai">Chennai</option>
      </select>
      <p>Selected city: {city}</p>
    </div>
  );
}
