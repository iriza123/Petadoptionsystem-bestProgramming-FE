import { useState, useEffect } from 'react'
import api from '../../services/api'
import './Adopter.css'

function MyRequests({ user }) {
  const [requests, setRequests] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    if (user) {
      fetchRequests()
    }
  }, [user])

  const fetchRequests = async () => {
    try {
      const response = await api.getUserRequests(user.userId)
      if (response.success) {
        setRequests(response.data)
      } else {
        setError('Failed to load requests')
      }
    } catch (err) {
      setError('An error occurred while loading your requests')
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return <div className="loading">Loading your requests...</div>
  }

  return (
    <div className="my-requests-container">
      <h1>My Adoption Requests</h1>

      {error && <div className="error-message">{error}</div>}

      {requests.length === 0 ? (
        <div className="no-requests">
          <p>You haven't submitted any adoption requests yet.</p>
        </div>
      ) : (
        <div className="requests-list">
          {requests.map(request => (
            <div key={request.id} className="request-card">
              <div className="request-header">
                <h3>Request #{request.id}</h3>
                <span className={`request-status ${request.status.toLowerCase()}`}>
                  {request.status}
                </span>
              </div>

              <div className="request-details">
                <p><strong>Pet ID:</strong> {request.petId}</p>
                <p><strong>Submitted:</strong> {new Date(request.requestDate).toLocaleDateString()}</p>

                <div className="request-reason">
                  <strong>Your reason:</strong>
                  <p>{request.reason}</p>
                </div>

                {request.status === 'APPROVED' && (
                  <div className="request-response success">
                    <strong>Congratulations!</strong> Your request was approved
                    {request.responseDate && (
                      <p>Approved on: {new Date(request.responseDate).toLocaleDateString()}</p>
                    )}
                    {request.adminNotes && (
                      <p>Admin notes: {request.adminNotes}</p>
                    )}
                  </div>
                )}

                {request.status === 'REJECTED' && (
                  <div className="request-response rejected">
                    <strong>Request Rejected</strong>
                    {request.responseDate && (
                      <p>Rejected on: {new Date(request.responseDate).toLocaleDateString()}</p>
                    )}
                    {request.adminNotes && (
                      <p>Admin notes: {request.adminNotes}</p>
                    )}
                  </div>
                )}

                {request.status === 'PENDING' && (
                  <div className="request-response pending">
                    <p>Your request is being reviewed by our team. You will be notified once a decision is made.</p>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default MyRequests
