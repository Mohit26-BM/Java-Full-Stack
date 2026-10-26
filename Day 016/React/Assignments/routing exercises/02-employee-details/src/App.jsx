import { useState } from 'react'
import { Navigate, Route, Routes } from 'react-router-dom'
import Layout from './components/Layout.jsx'
import { initialEmployees } from './employees.js'
import EmployeeDetailsPage from './pages/EmployeeDetailsPage.jsx'
import EmployeeEditPage from './pages/EmployeeEditPage.jsx'
import EmployeeListPage from './pages/EmployeeListPage.jsx'
import NotFoundPage from './pages/NotFoundPage.jsx'

function App() {
  const [employees, setEmployees] = useState(initialEmployees)

  const updateEmployee = (updated) => {
    setEmployees((current) => current.map((e) => (e.id === updated.id ? updated : e)))
  }

  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<Navigate to="/employees" replace />} />
        <Route path="employees" element={<EmployeeListPage employees={employees} />} />
        <Route path="employees/:employeeId" element={<EmployeeDetailsPage employees={employees} />} />
        <Route path="employees/:employeeId/edit" element={<EmployeeEditPage employees={employees} onSave={updateEmployee} />} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  )
}

export default App
