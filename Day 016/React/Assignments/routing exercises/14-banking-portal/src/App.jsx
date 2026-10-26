import { Navigate, Route, Routes } from 'react-router-dom'
import Layout from './components/Layout.jsx'
import RequireAuth from './components/RequireAuth.jsx'
import AccountDetailsPage from './pages/AccountDetailsPage.jsx'
import AccountsPage from './pages/AccountsPage.jsx'
import DashboardPage from './pages/DashboardPage.jsx'
import LoginPage from './pages/LoginPage.jsx'
import NotFoundPage from './pages/NotFoundPage.jsx'
import ProfilePage from './pages/ProfilePage.jsx'
import TransactionDetailsPage from './pages/TransactionDetailsPage.jsx'
import TransactionsPage from './pages/TransactionsPage.jsx'
import TransferConfirmPage from './pages/TransferConfirmPage.jsx'
import TransferPage from './pages/TransferPage.jsx'
import { BankProvider } from './state/BankContext.jsx'

function App({ initialState }) {
  return (
    <BankProvider initialState={initialState}>
      <Routes>
        <Route element={<Layout />}>
          <Route path="login" element={<LoginPage />} />
          <Route element={<RequireAuth />}>
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="dashboard" element={<DashboardPage />} />
            <Route path="accounts" element={<AccountsPage />} />
            <Route path="accounts/:accountId" element={<AccountDetailsPage />} />
            <Route path="transactions" element={<TransactionsPage />} />
            <Route path="transactions/:transactionId" element={<TransactionDetailsPage />} />
            <Route path="transfer" element={<TransferPage />} />
            <Route path="transfer/confirm" element={<TransferConfirmPage />} />
            <Route path="profile" element={<ProfilePage />} />
          </Route>
          <Route path="*" element={<NotFoundPage />} />
        </Route>
      </Routes>
    </BankProvider>
  )
}

export default App
