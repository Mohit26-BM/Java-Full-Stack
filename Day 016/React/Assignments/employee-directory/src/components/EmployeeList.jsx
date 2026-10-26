import { useEffect, useState } from 'react'
import { fetchEmployees } from '../api/employees.js'
import EmployeeCard from './EmployeeCard.jsx'

export default function EmployeeList() {
  const [employees, setEmployees] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [search, setSearch] = useState('')
  const [reloadCount, setReloadCount] = useState(0)

  useEffect(() => {
    let ignore = false
    setLoading(true)
    setError('')
    fetchEmployees()
      .then((users) => {
        if (!ignore) setEmployees(users)
      })
      .catch((err) => {
        if (!ignore) setError(`Could not load employees. ${err.message}`)
      })
      .finally(() => {
        if (!ignore) setLoading(false)
      })
    return () => {
      ignore = true
    }
  }, [reloadCount])

  const term = search.trim().toLowerCase()
  const visible = employees.filter(
    (e) => e.firstName.toLowerCase().includes(term) || e.lastName.toLowerCase().includes(term),
  )

  return (
    <>
      <div className="d-flex flex-column flex-sm-row gap-2 mb-3">
        <input
          type="search"
          className="form-control"
          placeholder="Search by first or last name"
          aria-label="Search by first or last name"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <button
          type="button"
          className="btn btn-primary text-nowrap"
          onClick={() => setReloadCount((n) => n + 1)}
          disabled={loading}
        >
          Reload
        </button>
      </div>

      {loading && <p className="text-secondary">Loading...</p>}

      {!loading && error && (
        <div className="alert alert-danger" role="alert">
          {error}
        </div>
      )}

      {!loading && !error && visible.length === 0 && <p className="text-secondary">No employees found</p>}

      {!loading && !error && visible.length > 0 && (
        <>
          <p className="text-secondary small">
            Showing {visible.length} of {employees.length} employees
          </p>
          <div className="row g-3">
            {visible.map((employee) => (
              <div key={employee.id} className="col-12 col-md-6 col-lg-4">
                <EmployeeCard employee={employee} />
              </div>
            ))}
          </div>
        </>
      )}
    </>
  )
}
