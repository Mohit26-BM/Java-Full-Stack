import { LANGUAGES, usePreferences } from './UserPreferencesContext.jsx'
import { TEXT } from './translations.js'

function Profile() {
  const { preferences } = usePreferences()
  return (
    <div className="prefs__card">
      <h2 className="prefs__title">{TEXT[preferences.language].profile}</h2>
      <dl className="prefs__list">
        <dt>Name</dt><dd>Asha Verma</dd>
        <dt>Theme</dt><dd>{preferences.theme}</dd>
        <dt>Language</dt><dd>{LANGUAGES[preferences.language]}</dd>
        <dt>Notifications</dt><dd>{preferences.notifications ? 'Enabled' : 'Disabled'}</dd>
      </dl>
    </div>
  )
}

export default Profile
