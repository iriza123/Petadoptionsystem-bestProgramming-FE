import { useState, useEffect } from 'react'
import api from '../../services/api'
import './Admin.css'

function Dashboard() {
  const [stats, setStats] = useState({
    totalPets: 0,
    availablePets: 0,
    adoptedPets: 0,
    pendingPets: 0,
    pendingRequests: 0
  })
  const [pets, setPets] = useState([])
  const [adoptedPets, setAdoptedPets] = useState([])
  const [requests, setRequests] = useState([])
  const [loading, setLoading] = useState(true)
  const [activeTab, setActiveTab] = useState('overview')

  useEffect(() => {
    fetchDashboardData()
  }, [])

  const fetchDashboardData = async () => {
    try {
      // Use getAllPetsAdmin to get ALL pets (AVAILABLE + PENDING + ADOPTED)
      const petsResponse = await api.getAllPetsAdmin()
      const requestsResponse = await api.getAllRequests()

      if (petsResponse.success && requestsResponse.success) {
        const allPets = petsResponse.data
        const allRequests = requestsResponse.data

        const adopted = allPets.filter(p => p.status === 'ADOPTED')

        setPets(allPets)
        setAdoptedPets(adopted)
        setRequests(allRequests)

        setStats({
          totalPets: allPets.length,
          availablePets: allPets.filter(p => p.status === 'AVAILABLE').length,
          adoptedPets: adopted.length,
          pendingPets: allPets.filter(p => p.status === 'PENDING').length,
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
        <div className="stat-card" onClick={() => setActiveTab('overview')} style={{cursor:'pointer'}}>
          <div className="stat-icon">🐾</div>
          <div className="stat-info">
            <h3>{stats.totalPets}</h3>
            <p>Total Pets</p>
          </div>
        </div>

        <div className="stat-card" onClick={() => setActiveTab('overview')} style={{cursor:'pointer'}}>
          <div className="stat-icon">✅</div>
          <div className="stat-info">
            <h3>{stats.availablePets}</h3>
            <p>Available</p>
          </div>
        </div>

        <div className="stat-card" onClick={() => setActiveTab('adopted')} style={{cursor:'pointer', border: activeTab === 'adopted' ? '2px solid #4CAF50' : ''}}>
          <div className="stat-icon">🏠</div>
          <div className="stat-info">
            <h3>{stats.adoptedPets}</h3>
            <p>Adopted</p>
          </div>
        </div>

        <div className="stat-card" onClick={() => setActiveTab('overview')} style={{cursor:'pointer'}}>
          <div className="stat-icon">⏳</div>
          <div className="stat-info">
            <h3>{stats.pendingRequests}</h3>
            <p>Pending Requests</p>
          </div>
        </div>
      </div>

      {/* Tab buttons */}
      <div style={{display:'flex', gap:'10px', margin:'20px 0'}}>
        <button
          onClick={() => setActiveTab('overview')}
          style={{padding:'8px 20px', borderRadius:'6px', border:'none', cursor:'pointer',
            background: activeTab === 'overview' ? '#4CAF50' : '#e0e0e0',
            color: activeTab === 'overview' ? 'white' : 'black', fontWeight:'bold'}}>
          Overview
        </button>
        <button
          onClick={() => setActiveTab('adopted')}
          style={{padding:'8px 20px', borderRadius:'6px', border:'none', cursor:'pointer',
            background: activeTab === 'adopted' ? '#4CAF50' : '#e0e0e0',
            color: activeTab === 'adopted' ? 'white' : 'black', fontWeight:'bold'}}>
          Adopted Pets ({stats.adoptedPets})
        </button>
      </div>

      {/* Overview Tab */}
      {activeTab === 'overview' && (
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
      )}

      {/* Adopted Pets Tab */}
      {activeTab === 'adopted' && (
        <div className="dashboard-section" style={{width:'100%'}}>
          <h2>Adopted Pets ({adoptedPets.length})</h2>
          {adoptedPets.length === 0 ? (
            <p>No pets have been adopted yet.</p>
          ) : (
            <table style={{width:'100%', borderCollapse:'collapse', marginTop:'10px'}}>
              <thead>
                <tr style={{background:'#f5f5f5'}}>
                  <th style={{padding:'10px', textAlign:'left', border:'1px solid #ddd'}}>ID</th>
                  <th style={{padding:'10px', textAlign:'left', border:'1px solid #ddd'}}>Name</th>
                  <th style={{padding:'10px', textAlign:'left', border:'1px solid #ddd'}}>Type</th>
                  <th style={{padding:'10px', textAlign:'left', border:'1px solid #ddd'}}>Breed</th>
                  <th style={{padding:'10px', textAlign:'left', border:'1px solid #ddd'}}>Age</th>
                  <th style={{padding:'10px', textAlign:'left', border:'1px solid #ddd'}}>Gender</th>
                  <th style={{padding:'10px', textAlign:'left', border:'1px solid #ddd'}}>Status</th>
                </tr>
              </thead>
              <tbody>
                {adoptedPets.map(pet => (
                  <tr key={pet.id}>
                    <td style={{padding:'10px', border:'1px solid #ddd'}}>{pet.id}</td>
                    <td style={{padding:'10px', border:'1px solid #ddd'}}>{pet.name}</td>
                    <td style={{padding:'10px', border:'1px solid #ddd'}}>{pet.type}</td>
                    <td style={{padding:'10px', border:'1px solid #ddd'}}>{pet.breed || '-'}</td>
                    <td style={{padding:'10px', border:'1px solid #ddd'}}>{pet.age}</td>
                    <td style={{padding:'10px', border:'1px solid #ddd'}}>{pet.gender}</td>
                    <td style={{padding:'10px', border:'1px solid #ddd'}}>
                      <span className="status-badge adopted">ADOPTED</span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}
    </div>
  )
}

export default Dashboard
