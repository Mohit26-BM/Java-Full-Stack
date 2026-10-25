interface Employee {
  id: number;
  name: string;
  active: boolean;
}

export default function FilterList() {
  const employees: Employee[] = [
    { id: 1, name: "Ravi", active: true },
    { id: 2, name: "Meena", active: false },
    { id: 3, name: "Sana", active: true }
  ];

  const activeEmployees = employees.filter(employee => employee.active);

  return (
    <ul>
      {activeEmployees.map(employee => (
        <li key={employee.id}>{employee.name}</li>
      ))}
    </ul>
  );
}
