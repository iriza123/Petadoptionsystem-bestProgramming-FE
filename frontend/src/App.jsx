import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { useState, useEffect } from 'react'
import Navbar from './components/Navbar'
import Login from './pages/auth/Login'
import Register from './pages/auth/Register'
import Home from './pages/adopter/Home'
import PetDetails from './pages/adopter/PetDetails'
import MyRequests from './pages/adopter/MyRequests'
import Dashboard from './pages/admin/Dashboard'
import ManagePets from './pages/admin/ManagePets'
import ManageRequests from './pages/admin/ManageRequests'
import './App.css'

function App() {
  const [user, setUser] = useState(null)

  useEffect(() => {
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      setUser(JSON.parse(storedUser))
    }
  }, [])

  const handleLogin = (userData) => {
    setUser(userData)
    localStorage.setItem('user', JSON.stringify(userData))
  }

  const handleLogout = () => {
    setUser(null)
    localStorage.removeItem('user')
  }

  // Component to handle root route based on user role
  const RootRoute = () => {
    if (user?.role === 'ADMIN') {
      return <Navigate to="/admin/dashboard" replace />
    }
    return <Home user={user} />
  }

  return (
    <Router>
      <div className="app">
        <Navbar user={user} onLogout={handleLogout} />
        <div className="container">
          <Routes>
            <Route path="/login" element={<Login onLogin={handleLogin} />} />
            <Route path="/register" element={<Register onLogin={handleLogin} />} />
            <Route path="/" element={<RootRoute />} />
            <Route path="/pets/:id" element={<PetDetails user={user} />} />
            <Route path="/my-requests" element={user ? <MyRequests user={user} /> : <Navigate to="/login" />} />
            <Route path="/admin/dashboard" element={user?.role === 'ADMIN' ? <Dashboard /> : <Navigate to="/" />} />
            <Route path="/admin/pets" element={user?.role === 'ADMIN' ? <ManagePets /> : <Navigate to="/" />} />
            <Route path="/admin/requests" element={user?.role === 'ADMIN' ? <ManageRequests /> : <Navigate to="/" />} />
          </Routes>
        </div>
      </div>
    </Router>
  )
}

export default App
