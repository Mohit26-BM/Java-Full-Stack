import { LANGUAGES, usePreferences } from './UserPreferencesContext.jsx'
import { TEXT } from './translations.js'

function Header() {
  const { preferences } = usePreferences()
  return (
    <header className="prefs__header">
      <strong>{TEXT[preferences.language].welcome}, Asha</strong>
      <span className="prefs__badges">
        <span className="prefs__badge" aria-label="Current theme">Theme: {preferences.theme}</span>
        <span className="prefs__badge" aria-label="Current language">{LANGUAGES[preferences.language]}</span>
        <span className="prefs__badge">{preferences.notifications ? '🔔 On' : '🔕 Off'}</span>
      </span>
    </header>
  )
}

export default Header
