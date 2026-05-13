import { Link } from 'react-router-dom'
import './Navbar.css'

function Navbar({ user, onLogout }) {
  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/" className="navbar-logo">
          🐾 Pet Adoption System
        </Link>

        <div className="navbar-menu">
          {(!user || user.role === 'ADOPTER') && (
            <Link to="/" className="navbar-link">Home</Link>
          )}

          {user ? (
            <>
              {user.role === 'ADOPTER' && (
                <Link to="/my-requests" className="navbar-link">My Requests</Link>
              )}

              {user.role === 'ADMIN' && (
                <>
                  <Link to="/admin/dashboard" className="navbar-link">Dashboard</Link>
                  <Link to="/admin/pets" className="navbar-link">Manage Pets</Link>
                  <Link to="/admin/requests" className="navbar-link">Manage Requests</Link>
                </>
              )}

              <span className="navbar-user">Hello, {user.name}</span>
              <button onClick={onLogout} className="navbar-logout-btn">Logout</button>
            </>
          ) : (
            <>
              <Link to="/login" className="navbar-link">Login</Link>
              <Link to="/register" className="navbar-link">Register</Link>
            </>
          )}
        </div>
      </div>
    </nav>
  )
}

export default Navbar
