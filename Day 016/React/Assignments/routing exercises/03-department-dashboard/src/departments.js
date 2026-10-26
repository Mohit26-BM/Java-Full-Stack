export const departments = [
  {
    id: 'engineering',
    name: 'Engineering',
    head: 'Manoj Pillai',
    location: 'Bengaluru',
    budget: 4500000,
    employees: [
      { id: 101, name: 'Asha Verma', role: 'Software Engineer' },
      { id: 102, name: 'Ravi Iyer', role: 'Backend Developer' },
      { id: 106, name: 'Manoj Pillai', role: 'Engineering Manager' },
    ],
    projects: [
      { id: 'P-11', name: 'Customer Portal', status: 'In progress' },
      { id: 'P-12', name: 'Payments API v2', status: 'Planned' },
    ],
    reports: [
      { id: 'R-1', title: 'Q3 delivery report', period: 'Jul–Sep 2026' },
      { id: 'R-2', title: 'Incident review', period: 'Sep 2026' },
    ],
  },
  {
    id: 'finance',
    name: 'Finance',
    head: 'Karan Shah',
    location: 'Pune',
    budget: 1200000,
    employees: [
      { id: 104, name: 'Karan Shah', role: 'Accountant' },
      { id: 108, name: 'Arjun Nair', role: 'Financial Analyst' },
    ],
    projects: [{ id: 'P-21', name: 'Payroll automation', status: 'In progress' }],
    reports: [{ id: 'R-3', title: 'Monthly close', period: 'Sep 2026' }],
  },
  {
    id: 'human-resources',
    name: 'Human Resources',
    head: 'Hana Kapoor',
    location: 'Mumbai',
    budget: 900000,
    employees: [
      { id: 103, name: 'Hana Kapoor', role: 'HR Manager' },
      { id: 105, name: 'Meena Das', role: 'HR Executive' },
    ],
    projects: [],
    reports: [{ id: 'R-4', title: 'Hiring summary', period: 'Q3 2026' }],
  },
]

export const findDepartment = (departmentId) => departments.find((d) => d.id === departmentId)
