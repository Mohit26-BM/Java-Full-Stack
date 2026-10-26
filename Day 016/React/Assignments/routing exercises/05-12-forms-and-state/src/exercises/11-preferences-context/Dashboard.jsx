import { usePreferences } from './UserPreferencesContext.jsx'
import { TEXT } from './translations.js'

function Dashboard() {
  const { preferences } = usePreferences()
  const text = TEXT[preferences.language]
  return (
    <div className="prefs__card">
      <h2 className="prefs__title">{text.dashboard}</h2>
      <p>{text.tasks}</p>
      <p className="muted">Showing the {preferences.theme} theme.</p>
    </div>
  )
}

export default Dashboard
