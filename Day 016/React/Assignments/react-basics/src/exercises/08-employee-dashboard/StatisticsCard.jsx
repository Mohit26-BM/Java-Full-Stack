function StatisticsCard({ label, value }) {
  return (
    <div className="stat-card">
      <p className="stat-card__label">{label}</p>
      <p className="stat-card__value" aria-label={label}>{value}</p>
    </div>
  )
}

export default StatisticsCard
