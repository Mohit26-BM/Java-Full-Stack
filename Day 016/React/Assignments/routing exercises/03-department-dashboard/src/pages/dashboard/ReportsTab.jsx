import { useOutletContext } from 'react-router-dom'

function ReportsTab() {
  const { reports } = useOutletContext()
  return (
    <div>
      <h2 className="tab-title">Reports</h2>
      <ul className="tab-list">
        {reports.map((report) => (
          <li key={report.id}>{report.title} <span className="muted">— {report.period}</span></li>
        ))}
      </ul>
    </div>
  )
}

export default ReportsTab
