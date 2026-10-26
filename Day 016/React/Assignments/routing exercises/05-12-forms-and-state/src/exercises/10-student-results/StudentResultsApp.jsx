import { useState } from 'react'
import ResultSummary from './ResultSummary.jsx'
import StudentFilter from './StudentFilter.jsx'
import StudentForm from './StudentForm.jsx'
import StudentList from './StudentList.jsx'
import './StudentResults.css'

export const PASS_MARK = 40

export const FILTERS = {
  All: () => true,
  Passed: (student) => student.marks >= PASS_MARK,
  Failed: (student) => student.marks < PASS_MARK,
}

function StudentResultsApp() {
  const [students, setStudents] = useState([])
  const [filter, setFilter] = useState('All')

  const addStudent = (student) => {
    setStudents((current) => [...current, { ...student, id: current.length + 1 }])
  }

  const visible = students.filter(FILTERS[filter])

  return (
    <section>
      <h1 className="page-title">Student Results</h1>
      <div className="results">
        <StudentForm onAdd={addStudent} />
        <div>
          <ResultSummary students={students} passMark={PASS_MARK} />
          <StudentFilter filters={Object.keys(FILTERS)} selected={filter} onSelect={setFilter} />
          <StudentList students={visible} passMark={PASS_MARK} emptyText={students.length === 0
            ? 'No students yet. Add one with the form.'
            : `No ${filter.toLowerCase()} students.`} />
        </div>
      </div>
    </section>
  )
}

export default StudentResultsApp
