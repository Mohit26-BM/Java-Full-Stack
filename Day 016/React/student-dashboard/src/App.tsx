
import { useState, type FormEvent } from "react";
import "./App.css";

type StudentStatus = "Active" | "Inactive";
type Tab = "students" | "reports" | "settings";

interface Student {
  id: number;
  name: string;
  course: string;
  marks: number;
  attendance: number;
  status: StudentStatus;
  city?: string;
}

type RequestState =
  | { status: "loading" }
  | { status: "error"; message: string }
  | { status: "success" };

const initialStudents: Student[] = [
  {
    id: 1,
    name: "Aarav Sharma",
    course: "React",
    marks: 86,
    attendance: 92,
    status: "Active",
    city: "Delhi",
  },
  {
    id: 2,
    name: "Priya Verma",
    course: "Python",
    marks: 34,
    attendance: 68,
    status: "Active",
    city: "Mumbai",
  },
  {
    id: 3,
    name: "Rohan Mehta",
    course: "Java",
    marks: 74,
    attendance: 79,
    status: "Inactive",
  },
  {
    id: 4,
    name: "Ananya Singh",
    course: "React",
    marks: 95,
    attendance: 98,
    status: "Active",
    city: "Pune",
  },
];

function getGrade(marks: number): string {
  if (marks >= 90) return "A+";
  if (marks >= 80) return "A";
  if (marks >= 70) return "B";
  if (marks >= 40) return "C";
  return "Fail";
}

function getStatusMessage(status: string): string {
  switch (status) {
    case "Active":
      return "Currently enrolled";
    case "Inactive":
      return "Enrollment paused";
    default:
      return "Unknown enrollment status";
  }
}

function App() {
  const [students, setStudents] = useState<Student[]>(initialStudents);

  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState("All");
  const [activeTab, setActiveTab] = useState<Tab>("students");
  const [isAdmin, setIsAdmin] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [deleteId, setDeleteId] = useState<number | null>(null);
  const [notification, setNotification] = useState("");
  const [request, setRequest] = useState<RequestState>({
    status: "success",
  });

  const [name, setName] = useState("");
  const [course, setCourse] = useState("React");
  const [marks, setMarks] = useState("50");
  const [attendance, setAttendance] = useState("80");

  const nameError = name.trim().length === 0;
  const marksNumber = Number(marks);
  const attendanceNumber = Number(attendance);

  const invalidMarks =
    marks.trim() === "" ||
    !Number.isFinite(marksNumber) ||
    marksNumber < 0 ||
    marksNumber > 100;

  const invalidAttendance =
    attendance.trim() === "" ||
    !Number.isFinite(attendanceNumber) ||
    attendanceNumber < 0 ||
    attendanceNumber > 100;

  const visibleStudents = students.filter((student) => {
    const matchesSearch = student.name
      .toLowerCase()
      .includes(search.trim().toLowerCase());

    const matchesFilter = filter === "All" || student.status === filter;

    return matchesSearch && matchesFilter;
  });

  const activeCount = students.filter(
    (student) => student.status === "Active",
  ).length;

  const averageMarks =
    students.length > 0
      ? Math.round(
          students.reduce((sum, student) => sum + student.marks, 0) /
            students.length,
        )
      : 0;

  function addStudent(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (nameError || invalidMarks || invalidAttendance) return;

    const newStudent: Student = {
      id: Date.now(),
      name: name.trim(),
      course,
      marks: marksNumber,
      attendance: attendanceNumber,
      status: "Active",
    };

    setStudents((previous) => [...previous, newStudent]);
    setName("");
    setCourse("React");
    setMarks("50");
    setAttendance("80");
    setShowForm(false);
    setNotification("Student added successfully.");
    setSearch("");
    setFilter("All");
    setActiveTab("students");
  }

  function deleteStudent() {
    if (deleteId === null) return;

    setStudents((previous) =>
      previous.filter((student) => student.id !== deleteId),
    );

    if (selectedId === deleteId) setSelectedId(null);

    setDeleteId(null);
    setNotification("Student deleted successfully.");
  }

  function simulateRequest() {
    setRequest({ status: "loading" });

    window.setTimeout(() => {
      setRequest({ status: "success" });
    }, 1000);
  }

  function renderEnrollmentStatus(status: string) {
    switch (status) {
      case "Active":
        return <span className="badge active">Active</span>;
      case "Inactive":
        return <span className="badge inactive">Inactive</span>;
      default:
        return <span className="badge">Unknown</span>;
    }
  }

  // Early return: conditional loading UI.
  if (request.status === "loading") {
    return (
      <main className="loading-screen">
        <div className="spinner" />
        <h2>Loading dashboard...</h2>
        <p>Please wait a moment.</p>
      </main>
    );
  }

  // Separate error state from the normal dashboard.
  if (request.status === "error") {
    return (
      <main className="loading-screen">
        <h2>Unable to load dashboard</h2>
        <p role="alert">{request.message}</p>
        <button onClick={simulateRequest}>Try again</button>
      </main>
    );
  }

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <h2 className="brand">
          Campus<span>Hub</span>
        </h2>
        <p className="sidebar-label">WORKSPACE</p>

        <button
          className={activeTab === "students" ? "nav active-nav" : "nav"}
          onClick={() => setActiveTab("students")}
        >
          Students
        </button>

        <button
          className={activeTab === "reports" ? "nav active-nav" : "nav"}
          onClick={() => setActiveTab("reports")}
        >
          Reports
        </button>

        <button
          className={activeTab === "settings" ? "nav active-nav" : "nav"}
          onClick={() => setActiveTab("settings")}
        >
          Settings
        </button>

        <div className="sidebar-bottom">
          <p>Demo account</p>
          <label className="role-toggle">
            <input
              type="checkbox"
              checked={isAdmin}
              onChange={(event) => setIsAdmin(event.target.checked)}
            />
            Admin mode
          </label>
        </div>
      </aside>

      <main className="main-content">
        <header className="topbar">
          <div>
            <p className="eyebrow">ACADEMIC OVERVIEW</p>
            <h1>Student Dashboard</h1>
          </div>

          <button
            className="secondary-button"
            onClick={() => setNotification("Your dashboard is up to date.")}
          >
            Check notifications
          </button>
        </header>

        {notification && (
          <div className="notification" role="status">
            <span>{notification}</span>
            <button
              aria-label="Dismiss notification"
              onClick={() => setNotification("")}
            >
              ×
            </button>
          </div>
        )}

        <section className="stats-grid">
          <article className="stat-card">
            <p>Total students</p>
            <h2>{students.length}</h2>
            <span>All enrolled records</span>
          </article>

          <article className="stat-card">
            <p>Active students</p>
            <h2>{activeCount}</h2>
            <span>
              {activeCount > 0
                ? "Students currently active"
                : "No active students"}
            </span>
          </article>

          <article className="stat-card">
            <p>Average marks</p>
            <h2>{averageMarks}%</h2>
            <span>
              {students.length === 0
                ? "No marks available"
                : averageMarks >= 75
                  ? "Strong class performance"
                  : "Room for improvement"}
            </span>
          </article>
        </section>

        {activeTab === "students" && (
          <section className="panel">
            <div className="panel-heading">
              <div>
                <h2>Student records</h2>
                <p>Manage enrollment and academic performance.</p>
              </div>

              {isAdmin && (
                <button
                  className="primary-button"
                  onClick={() => setShowForm(!showForm)}
                >
                  {showForm ? "Close form" : "+ Add student"}
                </button>
              )}
            </div>

            {showForm && isAdmin && (
              <form className="student-form" onSubmit={addStudent}>
                <h3>Add a student</h3>

                <label>
                  Full name
                  <input
                    value={name}
                    onChange={(event) => setName(event.target.value)}
                    placeholder="Enter student name"
                  />
                </label>

                {nameError && <p className="error-text">Name is required.</p>}

                <label>
                  Course
                  <select
                    value={course}
                    onChange={(event) => setCourse(event.target.value)}
                  >
                    <option>React</option>
                    <option>Python</option>
                    <option>Java</option>
                    <option>Data Science</option>
                  </select>
                </label>

                <div className="form-row">
                  <label>
                    Marks (0-100)
                    <input
                      type="number"
                      min="0"
                      max="100"
                      value={marks}
                      onChange={(event) => setMarks(event.target.value)}
                    />
                  </label>

                  <label>
                    Attendance (0-100)
                    <input
                      type="number"
                      min="0"
                      max="100"
                      value={attendance}
                      onChange={(event) => setAttendance(event.target.value)}
                    />
                  </label>
                </div>

                {invalidMarks && (
                  <p className="error-text">Marks must be between 0 and 100.</p>
                )}

                {invalidAttendance && (
                  <p className="error-text">
                    Attendance must be between 0 and 100.
                  </p>
                )}

                <button
                  className="primary-button"
                  type="submit"
                  disabled={nameError || invalidMarks || invalidAttendance}
                >
                  Save student
                </button>
              </form>
            )}

            <div className="toolbar">
              <input
                className="search-input"
                placeholder="Search by student name..."
                value={search}
                onChange={(event) => setSearch(event.target.value)}
              />

              <select
                aria-label="Filter by enrollment status"
                value={filter}
                onChange={(event) => setFilter(event.target.value)}
              >
                <option value="All">All statuses</option>
                <option value="Active">Active</option>
                <option value="Inactive">Inactive</option>
              </select>
            </div>

            <p className="result-count">
              Showing {visibleStudents.length} of {students.length} students
            </p>

            {visibleStudents.length === 0 ? (
              <div className="empty-state">
                <h3>No students found</h3>
                <p>
                  {students.length === 0
                    ? "Add a student to get started."
                    : search.trim() === "" && filter !== "All"
                      ? "No students match this status."
                      : "Try a different name or filter."}
                </p>
              </div>
            ) : (
              <div className="table-wrapper">
                <table>
                  <thead>
                    <tr>
                      <th>Student</th>
                      <th>Course</th>
                      <th>Marks</th>
                      <th>Attendance</th>
                      <th>Enrollment</th>
                      <th>Actions</th>
                    </tr>
                  </thead>

                  <tbody>
                    {visibleStudents.map((student) => (
                      <tr key={student.id}>
                        <td>
                          <strong>{student.name || "Guest"}</strong>
                          <small>{student.city ?? "City not provided"}</small>
                        </td>
                        <td>{student.course}</td>
                        <td>
                          <span
                            className={student.marks >= 40 ? "pass" : "fail"}
                          >
                            {student.marks}% · {getGrade(student.marks)}
                          </span>
                        </td>
                        <td>
                          <span
                            className={
                              student.attendance < 75 ? "fail" : "pass"
                            }
                          >
                            {student.attendance}%
                          </span>
                          {student.attendance < 75 && (
                            <small className="warning">Low attendance</small>
                          )}
                        </td>
                        <td>
                          {renderEnrollmentStatus(student.status)}
                          <small>{getStatusMessage(student.status)}</small>
                        </td>
                        <td>
                          <div className="actions">
                            <button
                              className="text-button"
                              onClick={() =>
                                setSelectedId(
                                  selectedId === student.id ? null : student.id,
                                )
                              }
                            >
                              {selectedId === student.id ? "Hide" : "Details"}
                            </button>

                            {isAdmin && (
                              <button
                                className="text-button danger"
                                onClick={() => setDeleteId(student.id)}
                              >
                                Delete
                              </button>
                            )}
                          </div>

                          {selectedId === student.id && (
                            <div className="details-box">
                              <p>Grade: {getGrade(student.marks)}</p>
                              <p>
                                Result: {student.marks >= 40 ? "Pass" : "Fail"}
                              </p>
                              <p>
                                {student.city
                                  ? `City: ${student.city}`
                                  : "City not provided."}
                              </p>
                            </div>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </section>
        )}

        {activeTab === "reports" && (
          <section className="panel">
            <h2>Academic reports</h2>
            <p>Summary of the current student records.</p>

            {students.length === 0 ? (
              <p>No data is available for reporting.</p>
            ) : (
              <>
                <div className="report-row">
                  <span>Students who passed</span>
                  <strong>
                    {students.filter((student) => student.marks >= 40).length}
                  </strong>
                </div>
                <div className="report-row">
                  <span>Students who failed</span>
                  <strong>
                    {students.filter((student) => student.marks < 40).length}
                  </strong>
                </div>
                <div className="report-row">
                  <span>Low attendance</span>
                  <strong>
                    {
                      students.filter((student) => student.attendance < 75)
                        .length
                    }
                  </strong>
                </div>
              </>
            )}

            <button className="secondary-button" onClick={simulateRequest}>
              Simulate loading
            </button>
          </section>
        )}

        {activeTab === "settings" && (
          <section className="panel">
            <h2>Settings</h2>
            <p>Choose which demo features are available.</p>

            <label className="role-toggle">
              <input
                type="checkbox"
                checked={isAdmin}
                onChange={(event) => setIsAdmin(event.target.checked)}
              />
              Enable admin controls
            </label>

            <p className="muted">
              {isAdmin
                ? "Admin actions are visible."
                : "Admin actions are hidden from this view."}
            </p>
          </section>
        )}
      </main>

      {deleteId !== null && (
        <div className="modal-backdrop">
          <section
            className="modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="delete-title"
          >
            <h2 id="delete-title">Delete student?</h2>
            <p>This action removes the student from the current list.</p>

            <div className="modal-actions">
              <button
                className="secondary-button"
                onClick={() => setDeleteId(null)}
              >
                Cancel
              </button>
              <button className="danger-button" onClick={deleteStudent}>
                Confirm delete
              </button>
            </div>
          </section>
        </div>
      )}
    </div>
  );
}

export default App;
