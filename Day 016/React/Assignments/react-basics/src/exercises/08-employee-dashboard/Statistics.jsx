import StatisticsCard from './StatisticsCard.jsx'
import { averageSalary, formatRupees } from './dashboardData.js'

function Statistics({ total, matching }) {
  return (
    <div className="dashboard__stats">
      <StatisticsCard label="Total employees" value={total} />
      <StatisticsCard label="Matching employees" value={matching.length} />
      <StatisticsCard label="Average salary" value={formatRupees(averageSalary(matching))} />
    </div>
  )
}

export default Statistics
