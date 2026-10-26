import { LANGUAGES, THEMES, usePreferences } from './UserPreferencesContext.jsx'

function Settings() {
  const { preferences, updatePreference } = usePreferences()
  return (
    <div className="prefs__card">
      <h2 className="prefs__title">Settings</h2>
      <label className="field">
        Theme
        <select className="input" value={preferences.theme} onChange={(e) => updatePreference('theme', e.target.value)}>
          {THEMES.map((t) => <option key={t} value={t}>{t}</option>)}
        </select>
      </label>
      <label className="field">
        Language
        <select className="input" value={preferences.language} onChange={(e) => updatePreference('language', e.target.value)}>
          {Object.entries(LANGUAGES).map(([code, name]) => <option key={code} value={code}>{name}</option>)}
        </select>
      </label>
      <label className="prefs__check">
        <input type="checkbox" checked={preferences.notifications}
          onChange={(e) => updatePreference('notifications', e.target.checked)} />
        Email notifications
      </label>
    </div>
  )
}

export default Settings
