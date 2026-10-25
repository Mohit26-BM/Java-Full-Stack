import { useState } from "react";

export default function RadioButtons() {
  const [course, setCourse] = useState<string>("React");
  const courses = ["React", "Java", "Python"];

  return (
    <div>
      {courses.map(item => (
        <label key={item} style={{ display: "block" }}>
          <input
            type="radio"
            name="course"
            value={item}
            checked={course === item}
            onChange={event => setCourse(event.target.value)}
          />
          {item}
        </label>
      ))}
      <p>Selected course: {course}</p>
    </div>
  );
}
