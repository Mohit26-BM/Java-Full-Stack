function AdminDashboardPage() {
  return (
    <section>
      <h1 className="page-title">Admin Dashboard</h1>
      <div className="stats">
        <div className="card"><p className="muted">Employees</p><p className="stats__value">128</p></div>
        <div className="card"><p className="muted">Open requests</p><p className="stats__value">7</p></div>
        <div className="card"><p className="muted">New this month</p><p className="stats__value">4</p></div>
      </div>
    </section>
  )
}

export default AdminDashboardPage
