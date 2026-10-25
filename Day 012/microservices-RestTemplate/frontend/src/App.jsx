import { Navigate, Route, Routes, useParams } from 'react-router-dom'
import { useAuth } from './auth.jsx'
import Layout from './components/Layout.jsx'
import ProtectedRoute from './components/ProtectedRoute.jsx'
import AdminCustomers from './pages/AdminCustomers.jsx'
import CustomerDetail from './pages/CustomerDetail.jsx'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'

function Home() {
  const { user, isAdmin } = useAuth()
  if (!user) return <Navigate to="/login" replace />
  return <Navigate to={isAdmin ? '/admin/customers' : '/dashboard'} replace />
}

function MyDashboard() {
  const { user } = useAuth()
  return <CustomerDetail customerId={user.customerId} />
}

function AdminCustomerPage() {
  const { id } = useParams()
  return <CustomerDetail customerId={Number(id)} showBack />
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/" element={<Home />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<Layout />}>
          <Route element={<ProtectedRoute role="CUSTOMER" />}>
            <Route path="/dashboard" element={<MyDashboard />} />
          </Route>
          <Route element={<ProtectedRoute role="ADMIN" />}>
            <Route path="/admin/customers" element={<AdminCustomers />} />
            <Route path="/admin/customers/:id" element={<AdminCustomerPage />} />
          </Route>
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
