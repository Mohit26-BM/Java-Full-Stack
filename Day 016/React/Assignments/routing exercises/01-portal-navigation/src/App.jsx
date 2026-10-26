import { Route, Routes } from 'react-router-dom'
import Layout from './components/Layout.jsx'
import AboutPage from './pages/AboutPage.jsx'
import DepartmentsPage from './pages/DepartmentsPage.jsx'
import EmployeesPage from './pages/EmployeesPage.jsx'
import HomePage from './pages/HomePage.jsx'
import NotFoundPage from './pages/NotFoundPage.jsx'

function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<HomePage />} />
        <Route path="employees" element={<EmployeesPage />} />
        <Route path="departments" element={<DepartmentsPage />} />
        <Route path="about" element={<AboutPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  )
}

export default App
