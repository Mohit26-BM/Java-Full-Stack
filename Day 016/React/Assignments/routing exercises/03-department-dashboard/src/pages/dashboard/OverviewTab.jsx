import { useOutletContext } from 'react-router-dom'

function OverviewTab() {
  const department = useOutletContext()
  return (
    <div>
      <h2 className="tab-title">Overview</h2>
      <dl className="overview">
        <div className="overview__item"><dt>Employees</dt><dd>{department.employees.length}</dd></div>
        <div className="overview__item"><dt>Projects</dt><dd>{department.projects.length}</dd></div>
        <div className="overview__item"><dt>Reports</dt><dd>{department.reports.length}</dd></div>
        <div className="overview__item"><dt>Budget</dt><dd>₹{department.budget.toLocaleString('en-IN')}</dd></div>
      </dl>
    </div>
  )
}

export default OverviewTab
