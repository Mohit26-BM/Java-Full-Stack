interface Student {
  id: number;
  name: string;
  course: string;
}

export default function ObjectList() {
  const students: Student[] = [
    { id: 1, name: "Ananya", course: "React" },
    { id: 2, name: "Rahul", course: "Java" },
    { id: 3, name: "Kiran", course: "Python" }
  ];

  return (
    <div>
      {students.map(student => (
        <article key={student.id}>
          <h3>{student.name}</h3>
          <p>Course: {student.course}</p>
        </article>
      ))}
    </div>
  );
}
