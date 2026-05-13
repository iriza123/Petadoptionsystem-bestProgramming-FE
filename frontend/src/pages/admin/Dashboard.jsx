import { useState, useEffect } from 'react'
import api from '../../services/api'
import './Admin.css'

function Dashboard() {
  const [stats, setStats] = useState({
    totalPets: 0,
    availablePets: 0,
    adoptedPets: 0,
    pendingRequests: 0
  })
  const [pets, setPets] = useState([])
  const [requests, setRequests] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchDashboardData()
  }, [])

  const fetchDashboardData = async () => {
    try {
      const petsResponse = await api.getAllPets()
      const requestsResponse = await api.getAllRequests()

      if (petsResponse.success && requestsResponse.success) {
        const allPets = petsResponse.data
        const allRequests = requestsResponse.data

        setPets(allPets)
        setRequests(allRequests)

        setStats({
          totalPets: allPets.length,
          availablePets: allPets.filter(p => p.status === 'AVAILABLE').length,
          adoptedPets: allPets.filter(p => p.status === 'ADOPTED').length,
          pendingRequests: allRequests.filter(r => r.status === 'PENDING').length
        })
      }
    } catch (err) {
      console.error('Error fetching dashboard data:', err)
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return <div className="loading">Loading dashboard...</div>
  }

  return (
    <div className="dashboard-container">
      <h1>Admin Dashboard</h1>

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon">🐾</div>
          <div className="stat-info">
            <h3>{stats.totalPets}</h3>
            <p>Total Pets</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">✅</div>
          <div className="stat-info">
            <h3>{stats.availablePets}</h3>
            <p>Available</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">🏠</div>
          <div className="stat-info">
            <h3>{stats.adoptedPets}</h3>
            <p>Adopted</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">⏳</div>
          <div className="stat-info">
            <h3>{stats.pendingRequests}</h3>
            <p>Pending Requests</p>
          </div>
        </div>
      </div>

      <div className="dashboard-sections">
        <div className="dashboard-section">
          <h2>Recent Adoption Requests</h2>
          {requests.length === 0 ? (
            <p>No adoption requests yet</p>
          ) : (
            <div className="recent-requests">
              {requests.slice(0, 5).map(request => (
                <div key={request.id} className="recent-item">
                  <span className={`status-badge ${request.status.toLowerCase()}`}>
                    {request.status}
                  </span>
                  <span>Request #{request.id} - Pet #{request.petId}</span>
                  <span className="date">
                    {new Date(request.requestDate).toLocaleDateString()}
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="dashboard-section">
          <h2>Pet Status Overview</h2>
          <div className="pet-status-list">
            {pets.slice(0, 5).map(pet => (
              <div key={pet.id} className="pet-status-item">
                <span>{pet.name}</span>
                <span className={`status-badge ${pet.status.toLowerCase()}`}>
                  {pet.status}
                </span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}

export default Dashboard
