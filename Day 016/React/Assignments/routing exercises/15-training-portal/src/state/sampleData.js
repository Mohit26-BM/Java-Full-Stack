export const CATEGORIES = ['Programming', 'Cloud', 'Data', 'Soft Skills']
export const EXPERIENCE_LEVELS = ['Beginner', 'Intermediate', 'Advanced']

export const sampleData = {
  trainers: [
    { id: 'T-1', name: 'Dr. Meera Nair', expertise: 'Java & Spring', email: 'meera.nair@training.com' },
    { id: 'T-2', name: 'Arjun Rao', expertise: 'React & Front-end', email: 'arjun.rao@training.com' },
    { id: 'T-3', name: 'Fatima Khan', expertise: 'Cloud & DevOps', email: 'fatima.khan@training.com' },
    { id: 'T-4', name: 'Vikas Sharma', expertise: 'Communication', email: 'vikas.sharma@training.com' },
  ],
  participants: [
    { id: 'P-101', name: 'Asha Verma', email: 'asha.verma@company.com', phone: '9876543210', organization: 'Acme Ltd', level: 'Beginner' },
    { id: 'P-102', name: 'Ravi Iyer', email: 'ravi.iyer@company.com', phone: '9123456780', organization: 'Acme Ltd', level: 'Intermediate' },
    { id: 'P-103', name: 'Neha Joshi', email: 'neha.joshi@globex.com', phone: '8899776655', organization: 'Globex', level: 'Beginner' },
    { id: 'P-104', name: 'Karan Shah', email: 'karan.shah@globex.com', phone: '7766554433', organization: 'Globex', level: 'Advanced' },
  ],
  courses: [
    { id: 'C-201', title: 'Spring Boot Fundamentals', category: 'Programming', trainerId: 'T-1', startDate: '2026-11-02', durationDays: 5, capacity: 20 },
    { id: 'C-202', title: 'React for Beginners', category: 'Programming', trainerId: 'T-2', startDate: '2026-11-09', durationDays: 4, capacity: 3 },
    { id: 'C-203', title: 'AWS Cloud Practitioner', category: 'Cloud', trainerId: 'T-3', startDate: '2026-11-16', durationDays: 3, capacity: 15 },
    { id: 'C-204', title: 'Presentation Skills', category: 'Soft Skills', trainerId: 'T-4', startDate: '2026-10-20', durationDays: 2, capacity: 25 },
  ],
  enrollments: [
    { id: 'E-301', participantId: 'P-101', courseId: 'C-201', date: '2026-10-10', status: 'Enrolled' },
    { id: 'E-302', participantId: 'P-102', courseId: 'C-202', date: '2026-10-11', status: 'Enrolled' },
    { id: 'E-303', participantId: 'P-103', courseId: 'C-202', date: '2026-10-12', status: 'Enrolled' },
    { id: 'E-304', participantId: 'P-104', courseId: 'C-204', date: '2026-10-01', status: 'Completed' },
    { id: 'E-305', participantId: 'P-101', courseId: 'C-204', date: '2026-10-02', status: 'Completed' },
  ],
}
