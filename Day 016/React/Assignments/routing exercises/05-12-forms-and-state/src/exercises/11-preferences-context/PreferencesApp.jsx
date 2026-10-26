import Dashboard from './Dashboard.jsx'
import Header from './Header.jsx'
import Profile from './Profile.jsx'
import Settings from './Settings.jsx'
import { UserPreferencesProvider, usePreferences } from './UserPreferencesContext.jsx'
import './Preferences.css'

function ThemedArea() {
  const { preferences } = usePreferences()
  return (
    <div className={`prefs prefs--${preferences.theme}`} data-testid="themed-area">
      <Header />
      <div className="prefs__grid">
        <Settings />
        <Dashboard />
        <Profile />
      </div>
    </div>
  )
}

function PreferencesApp() {
  return (
    <section>
      <h1 className="page-title">Employee Preferences (Context API)</h1>
      <UserPreferencesProvider>
        <ThemedArea />
      </UserPreferencesProvider>
    </section>
  )
}

export default PreferencesApp
