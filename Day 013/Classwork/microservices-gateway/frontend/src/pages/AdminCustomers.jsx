import { useCallback, useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api.js'
import CustomerForm from '../components/CustomerForm.jsx'
import { ErrorAlert, Spinner, SuccessAlert } from '../components/Feedback.jsx'
import { formatDate, titleCase } from '../format.js'

export default function AdminCustomers() {
  const [customers, setCustomers] = useState(null)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const [query, setQuery] = useState('')
  const [adding, setAdding] = useState(false)

  const load = useCallback(async () => {
    try {
      setCustomers(await api.customers())
    } catch (err) {
      setError(err.message)
    }
  }, [])

  useEffect(() => {
    load()
  }, [load])

  const visible = useMemo(() => {
    const q = query.trim().toLowerCase()
    if (!customers) return []
    return q
      ? customers.filter((c) => [c.name, c.email, c.city, c.mobile, String(c.customerId)].some((v) => v.toLowerCase().includes(q)))
      : customers
  }, [customers, query])

  return (
    <>
      <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
        <h1 className="h3 mb-0">Customers</h1>
        {!adding && <button className="btn btn-primary" onClick={() => setAdding(true)}>Add customer</button>}
      </div>

      <ErrorAlert message={error} />
      <SuccessAlert message={notice} />

      {adding && (
        <div className="card mb-4">
          <div className="card-header card-header-accent">New customer</div>
          <div className="card-body">
            <p className="text-secondary small">
              Customers added here have no login. Ask them to use <strong>Create an account</strong> to get online access.
            </p>
            <CustomerForm
              submitLabel="Add customer"
              onCancel={() => setAdding(false)}
              onSubmit={async (payload) => {
                const created = await api.addCustomer(payload)
                setNotice(`Customer ${created.name} added (ID ${created.customerId}).`)
                setAdding(false)
                await load()
              }}
            />
          </div>
        </div>
      )}

      <div className="card hover-lift">
        <div className="card-body">
          <input className="form-control mb-3" placeholder="Search by name, email, city, mobile or ID"
            aria-label="Search customers" value={query} onChange={(e) => setQuery(e.target.value)} />
          {!customers && !error && <Spinner />}
          {customers && (
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead>
                  <tr>
                    <th>ID</th><th>Name</th><th>Email</th><th>Mobile</th><th>City</th><th>Gender</th><th>Date of birth</th><th />
                  </tr>
                </thead>
                <tbody>
                  {visible.map((c) => (
                    <tr key={c.customerId}>
                      <td>{c.customerId}</td>
                      <td>{c.name}</td>
                      <td>{c.email}</td>
                      <td>{c.mobile}</td>
                      <td>{c.city}</td>
                      <td>{titleCase(c.gender)}</td>
                      <td>{formatDate(c.dateOfBirth)}</td>
                      <td className="text-end">
                        <Link className="btn btn-sm btn-outline-primary" to={`/admin/customers/${c.customerId}`}>View</Link>
                      </td>
                    </tr>
                  ))}
                  {visible.length === 0 && (
                    <tr><td colSpan={8} className="text-center text-secondary py-4">No customers found.</td></tr>
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </>
  )
}
