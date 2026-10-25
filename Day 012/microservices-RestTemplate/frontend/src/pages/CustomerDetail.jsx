import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api.js'
import CountUp from '../components/CountUp.jsx'
import { ErrorAlert, Spinner, SuccessAlert } from '../components/Feedback.jsx'
import { formatDate, formatMoney, titleCase } from '../format.js'

const LOAN_TYPES = ['HOME', 'CAR', 'PERSONAL', 'EDUCATION']

function OpenSavingsForm({ customerId, onDone }) {
  const [deposit, setDeposit] = useState('')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    setBusy(true)
    try {
      await api.openSavings({ customerId, initialDeposit: Number(deposit) })
      setDeposit('')
      await onDone('Savings account opened.')
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <form onSubmit={submit} className="border-top pt-3 mt-3">
      <ErrorAlert message={error} />
      <div className="row g-2 align-items-end">
        <div className="col-sm-8">
          <label htmlFor="deposit" className="form-label">Open a new account - initial deposit (INR)</label>
          <input id="deposit" type="number" min="0.01" step="0.01" className="form-control" value={deposit}
            onChange={(e) => setDeposit(e.target.value)} required />
        </div>
        <div className="col-sm-4">
          <button className="btn btn-primary w-100" disabled={busy}>{busy ? 'Opening...' : 'Open account'}</button>
        </div>
      </div>
    </form>
  )
}

function LoanForm({ customerId, onDone }) {
  const [form, setForm] = useState({ loanType: 'HOME', principalAmount: '', interestRate: '', tenureMonths: '' })
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  const update = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    setBusy(true)
    try {
      await api.createLoan({
        customerId,
        loanType: form.loanType,
        principalAmount: Number(form.principalAmount),
        interestRate: Number(form.interestRate),
        tenureMonths: Number(form.tenureMonths),
      })
      setForm({ loanType: 'HOME', principalAmount: '', interestRate: '', tenureMonths: '' })
      await onDone('Loan created.')
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <form onSubmit={submit} className="border-top pt-3 mt-3">
      <ErrorAlert message={error} />
      <div className="row g-2 align-items-end">
        <div className="col-sm-6 col-lg-3">
          <label htmlFor="loanType" className="form-label">Loan type</label>
          <select id="loanType" name="loanType" className="form-select" value={form.loanType} onChange={update}>
            {LOAN_TYPES.map((t) => <option key={t} value={t}>{titleCase(t)}</option>)}
          </select>
        </div>
        <div className="col-sm-6 col-lg-3">
          <label htmlFor="principalAmount" className="form-label">Principal (INR)</label>
          <input id="principalAmount" name="principalAmount" type="number" min="0.01" step="0.01"
            className="form-control" value={form.principalAmount} onChange={update} required />
        </div>
        <div className="col-sm-6 col-lg-2">
          <label htmlFor="interestRate" className="form-label">Rate (%)</label>
          <input id="interestRate" name="interestRate" type="number" min="0.01" max="100" step="0.01"
            className="form-control" value={form.interestRate} onChange={update} required />
        </div>
        <div className="col-sm-6 col-lg-2">
          <label htmlFor="tenureMonths" className="form-label">Tenure (months)</label>
          <input id="tenureMonths" name="tenureMonths" type="number" min="1" max="360" step="1"
            className="form-control" value={form.tenureMonths} onChange={update} required />
        </div>
        <div className="col-lg-2">
          <button className="btn btn-primary w-100" disabled={busy}>{busy ? 'Saving...' : 'Add loan'}</button>
        </div>
      </div>
    </form>
  )
}

export default function CustomerDetail({ customerId, showBack = false }) {
  const [customer, setCustomer] = useState(null)
  const [savings, setSavings] = useState(null)
  const [loans, setLoans] = useState(null)
  const [error, setError] = useState('')
  const [accountsError, setAccountsError] = useState('')
  const [notice, setNotice] = useState('')

  const loadAccounts = useCallback(async () => {
    setAccountsError('')
    try {
      const [s, l] = await Promise.all([api.savings(customerId), api.loans(customerId)])
      setSavings(s)
      setLoans(l)
    } catch (err) {
      setAccountsError(err.message)
    }
  }, [customerId])

  useEffect(() => {
    let cancelled = false
    setCustomer(null)
    setSavings(null)
    setLoans(null)
    setError('')
    api.customer(customerId)
      .then((c) => !cancelled && setCustomer(c))
      .catch((err) => !cancelled && setError(err.message))
    loadAccounts()
    return () => {
      cancelled = true
    }
  }, [customerId, loadAccounts])

  const refreshed = async (message) => {
    setNotice(message)
    await loadAccounts()
  }

  if (error) {
    return (
      <>
        {showBack && <Link to="/admin/customers">&larr; Back to customers</Link>}
        <div className="mt-3"><ErrorAlert message={error} /></div>
      </>
    )
  }
  if (!customer) return <Spinner />

  const totalBalance = (savings ?? []).reduce((sum, a) => sum + Number(a.balance), 0)
  const totalLoans = (loans ?? []).reduce((sum, l) => sum + Number(l.principalAmount), 0)

  return (
    <>
      {showBack && <Link to="/admin/customers" className="d-inline-block mb-2">&larr; Back to customers</Link>}
      <h1 className="h3 mb-3">{showBack ? customer.name : `Welcome, ${customer.name}`}</h1>
      <SuccessAlert message={notice} />

      <div className="row g-3 mb-4 stagger">
        <div className="col-md-6 col-lg-4">
          <div className="stat-tile p-3 h-100">
            <div className="label">Total savings balance</div>
            <div className="fs-4 fw-semibold">{savings ? <CountUp value={totalBalance} /> : '...'}</div>
          </div>
        </div>
        <div className="col-md-6 col-lg-4">
          <div className="stat-tile p-3 h-100">
            <div className="label">Total loan principal</div>
            <div className="fs-4 fw-semibold">{loans ? <CountUp value={totalLoans} /> : '...'}</div>
          </div>
        </div>
      </div>

      <div className="card mb-4 hover-lift">
        <div className="card-header card-header-accent">Profile</div>
        <div className="card-body">
          <dl className="row mb-0">
            <dt className="col-sm-3">Customer ID</dt><dd className="col-sm-9">{customer.customerId}</dd>
            <dt className="col-sm-3">Email</dt><dd className="col-sm-9">{customer.email}</dd>
            <dt className="col-sm-3">Mobile</dt><dd className="col-sm-9">{customer.mobile}</dd>
            <dt className="col-sm-3">Date of birth</dt><dd className="col-sm-9">{formatDate(customer.dateOfBirth)}</dd>
            <dt className="col-sm-3">Gender</dt><dd className="col-sm-9">{titleCase(customer.gender)}</dd>
            <dt className="col-sm-3">City</dt><dd className="col-sm-9 mb-0">{customer.city}</dd>
          </dl>
        </div>
      </div>

      <ErrorAlert message={accountsError} />

      <div className="card mb-4 hover-lift">
        <div className="card-header card-header-accent">Savings accounts</div>
        <div className="card-body">
          {!savings && !accountsError && <Spinner />}
          {savings && (
            <div className="table-responsive">
              <table className="table align-middle mb-0">
                <thead><tr><th>Account number</th><th>Balance</th><th>Interest rate</th><th>Opened</th></tr></thead>
                <tbody>
                  {savings.map((a) => (
                    <tr key={a.accountId}>
                      <td>{a.accountNumber}</td>
                      <td>{formatMoney(a.balance)}</td>
                      <td>{a.interestRate}%</td>
                      <td>{formatDate(a.openedOn)}</td>
                    </tr>
                  ))}
                  {savings.length === 0 && (
                    <tr><td colSpan={4} className="text-center text-secondary py-3">No savings accounts yet.</td></tr>
                  )}
                </tbody>
              </table>
            </div>
          )}
          <OpenSavingsForm customerId={customer.customerId} onDone={refreshed} />
        </div>
      </div>

      <div className="card hover-lift">
        <div className="card-header card-header-accent">Loans</div>
        <div className="card-body">
          {!loans && !accountsError && <Spinner />}
          {loans && (
            <div className="table-responsive">
              <table className="table align-middle mb-0">
                <thead><tr><th>Type</th><th>Principal</th><th>Rate</th><th>Tenure</th><th>Start date</th></tr></thead>
                <tbody>
                  {loans.map((l) => (
                    <tr key={l.loanId}>
                      <td>{titleCase(l.loanType)}</td>
                      <td>{formatMoney(l.principalAmount)}</td>
                      <td>{l.interestRate}%</td>
                      <td>{l.tenureMonths} months</td>
                      <td>{formatDate(l.startDate)}</td>
                    </tr>
                  ))}
                  {loans.length === 0 && (
                    <tr><td colSpan={5} className="text-center text-secondary py-3">No loans yet.</td></tr>
                  )}
                </tbody>
              </table>
            </div>
          )}
          <LoanForm customerId={customer.customerId} onDone={refreshed} />
        </div>
      </div>
    </>
  )
}
