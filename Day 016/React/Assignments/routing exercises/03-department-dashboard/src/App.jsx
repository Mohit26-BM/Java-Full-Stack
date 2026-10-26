import { Link, Navigate, Outlet, Route, Routes } from 'react-router-dom'
import DepartmentDashboard from './pages/DepartmentDashboard.jsx'
import DepartmentListPage from './pages/DepartmentListPage.jsx'
import NotFoundPage from './pages/NotFoundPage.jsx'
import EmployeesTab from './pages/dashboard/EmployeesTab.jsx'
import OverviewTab from './pages/dashboard/OverviewTab.jsx'
import ProjectsTab from './pages/dashboard/ProjectsTab.jsx'
import ReportsTab from './pages/dashboard/ReportsTab.jsx'

function Shell() {
  return (
    <>
      <header className="app-header">
        <Link to="/departments" className="app-title">Department Dashboard</Link>
      </header>
      <main className="app-main">
        <Outlet />
      </main>
    </>
  )
}

function App() {
  return (
    <Routes>
      <Route element={<Shell />}>
        <Route index element={<Navigate to="/departments" replace />} />
        <Route path="departments" element={<DepartmentListPage />} />
        <Route path="departments/:departmentId" element={<DepartmentDashboard />}>
          <Route index element={<Navigate to="overview" replace />} />
          <Route path="overview" element={<OverviewTab />} />
          <Route path="employees" element={<EmployeesTab />} />
          <Route path="projects" element={<ProjectsTab />} />
          <Route path="reports" element={<ReportsTab />} />
          <Route path="*" element={<p className="muted">There is no such section in this dashboard.</p>} />
        </Route>
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  )
}

export default App
