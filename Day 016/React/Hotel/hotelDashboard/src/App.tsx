import { BrowserRouter, Route, Routes } from 'react-router-dom'
import AuthProvider from './AuthProvider'
import NavBar from './components/NavBar'
import ProtectedRoute from './components/ProtectedRoute'
import Login from './pages/Login'
import ViewHotels from './pages/ViewHotels'
import AddHotel from './pages/AddHotel'
import UpdateHotels from './pages/UpdateHotels'
import EditHotel from './pages/EditHotel'
import DeleteHotels from './pages/DeleteHotels'
import SearchHotels from './pages/SearchHotels'

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <NavBar />

        <main className="container pb-5">
          <Routes>
            <Route path="/login" element={<Login />} />

            {/* Everything below needs a login token */}
            <Route element={<ProtectedRoute />}>
              <Route path="/" element={<ViewHotels />} />
              <Route path="/add" element={<AddHotel />} />
              <Route path="/update" element={<UpdateHotels />} />
              <Route path="/update/:id" element={<EditHotel />} />
              <Route path="/delete" element={<DeleteHotels />} />
              <Route path="/search" element={<SearchHotels />} />
            </Route>
          </Routes>
        </main>
      </BrowserRouter>
    </AuthProvider>
  );
}
export default App
