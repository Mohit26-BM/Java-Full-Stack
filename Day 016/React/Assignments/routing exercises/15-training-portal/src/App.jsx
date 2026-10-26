import { Navigate, Route, Routes } from 'react-router-dom'
import Layout from './components/Layout.jsx'
import CourseCreatePage from './pages/CourseCreatePage.jsx'
import CourseDetailsPage from './pages/CourseDetailsPage.jsx'
import CoursesPage from './pages/CoursesPage.jsx'
import DashboardPage from './pages/DashboardPage.jsx'
import EnrollmentsPage from './pages/EnrollmentsPage.jsx'
import NotFoundPage from './pages/NotFoundPage.jsx'
import ParticipantDetailsPage from './pages/ParticipantDetailsPage.jsx'
import ParticipantRegisterPage from './pages/ParticipantRegisterPage.jsx'
import ParticipantsPage from './pages/ParticipantsPage.jsx'
import ReportsPage from './pages/ReportsPage.jsx'
import TrainersPage from './pages/TrainersPage.jsx'
import { TrainingProvider } from './state/TrainingContext.jsx'

function App() {
  return (
    <TrainingProvider>
      <Routes>
        <Route element={<Layout />}>
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="dashboard" element={<DashboardPage />} />
          <Route path="trainers" element={<TrainersPage />} />
          <Route path="participants" element={<ParticipantsPage />} />
          <Route path="participants/new" element={<ParticipantRegisterPage />} />
          <Route path="participants/:participantId" element={<ParticipantDetailsPage />} />
          <Route path="courses" element={<CoursesPage />} />
          <Route path="courses/new" element={<CourseCreatePage />} />
          <Route path="courses/:courseId" element={<CourseDetailsPage />} />
          <Route path="enrollments" element={<EnrollmentsPage />} />
          <Route path="reports" element={<ReportsPage />} />
          <Route path="*" element={<NotFoundPage />} />
        </Route>
      </Routes>
    </TrainingProvider>
  )
}

export default App
