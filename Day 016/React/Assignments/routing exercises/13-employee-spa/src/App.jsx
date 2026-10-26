import { Navigate, Route, Routes } from 'react-router-dom'
import Layout from './components/Layout.jsx'
import { EmployeeProvider } from './context/EmployeeContext.jsx'
import EmployeeCreatePage from './pages/EmployeeCreatePage.jsx'
import EmployeeDetailsPage from './pages/EmployeeDetailsPage.jsx'
import EmployeeEditPage from './pages/EmployeeEditPage.jsx'
import EmployeeListPage from './pages/EmployeeListPage.jsx'
import NotFoundPage from './pages/NotFoundPage.jsx'

function App({ initialEmployees }) {
  return (
    <EmployeeProvider initialEmployees={initialEmployees}>
      <Routes>
        <Route element={<Layout />}>
          <Route index element={<Navigate to="/employees" replace />} />
          <Route path="employees" element={<EmployeeListPage />} />
          <Route path="employees/new" element={<EmployeeCreatePage />} />
          <Route path="employees/:id" element={<EmployeeDetailsPage />} />
          <Route path="employees/:id/edit" element={<EmployeeEditPage />} />
          <Route path="*" element={<NotFoundPage />} />
        </Route>
      </Routes>
    </EmployeeProvider>
  )
}

export default App
