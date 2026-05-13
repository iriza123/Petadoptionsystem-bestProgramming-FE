import { useState, useEffect } from 'react'
import api from '../../services/api'
import './Admin.css'

function ManageRequests() {
  const [requests, setRequests] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [filter, setFilter] = useState('ALL')
  const [selectedRequest, setSelectedRequest] = useState(null)
  const [adminNotes, setAdminNotes] = useState('')

  useEffect(() => {
    fetchRequests()
  }, [])

  const fetchRequests = async () => {
    try {
      const response = await api.getAllRequests()
      if (response.success) {
        setRequests(response.data)
      } else {
        setError('Failed to load requests')
      }
    } catch (err) {
      setError('An error occurred while loading requests')
    } finally {
      setLoading(false)
    }
  }

  const handleApprove = async (requestId) => {
    try {
      const response = await api.approveRequest(requestId, adminNotes)
      if (response.success) {
        alert('Adoption request approved successfully!')
        fetchRequests()
        setSelectedRequest(null)
        setAdminNotes('')
      } else {
        setError(response.message)
      }
    } catch (err) {
      setError('Failed to approve request')
    }
  }

  const handleReject = async (requestId) => {
    if (!window.confirm('Are you sure you want to reject this request?')) {
      return
    }

    try {
      const response = await api.rejectRequest(requestId, adminNotes)
      if (response.success) {
        alert('Adoption request rejected')
        fetchRequests()
        setSelectedRequest(null)
        setAdminNotes('')
      } else {
        setError(response.message)
      }
    } catch (err) {
      setError('Failed to reject request')
    }
  }

  const filteredRequests = filter === 'ALL'
    ? requests
    : requests.filter(req => req.status === filter)

  if (loading) {
    return <div className="loading">Loading requests...</div>
  }

  return (
    <div className="manage-requests-container">
      <h1>Manage Adoption Requests</h1>

      {error && <div className="error-message">{error}</div>}

      <div className="filter-section">
        <label>Filter by status: </label>
        <select value={filter} onChange={(e) => setFilter(e.target.value)} className="filter-select">
          <option value="ALL">All Requests</option>
          <option value="PENDING">Pending</option>
          <option value="APPROVED">Approved</option>
          <option value="REJECTED">Rejected</option>
        </select>
      </div>

      {filteredRequests.length === 0 ? (
        <div className="no-requests">
          <p>No {filter.toLowerCase()} requests found</p>
        </div>
      ) : (
        <div className="requests-table">
          <table>
            <thead>
              <tr>
                <th>Request ID</th>
                <th>User ID</th>
                <th>Pet ID</th>
                <th>Reason</th>
                <th>Status</th>
                <th>Submitted</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredRequests.map(request => (
                <tr key={request.id}>
                  <td>{request.id}</td>
                  <td>{request.userId}</td>
                  <td>{request.petId}</td>
                  <td className="reason-cell">
                    {request.reason.length > 50
                      ? request.reason.substring(0, 50) + '...'
                      : request.reason
                    }
                  </td>
                  <td>
                    <span className={`status-badge ${request.status.toLowerCase()}`}>
                      {request.status}
                    </span>
                  </td>
                  <td>{new Date(request.requestDate).toLocaleDateString()}</td>
                  <td>
                    {request.status === 'PENDING' ? (
                      <button
                        onClick={() => setSelectedRequest(request)}
                        className="review-btn"
                      >
                        Review
                      </button>
                    ) : (
                      <button
                        onClick={() => setSelectedRequest(request)}
                        className="view-btn"
                      >
                        View
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {selectedRequest && (
        <div className="modal-overlay" onClick={() => setSelectedRequest(null)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Adoption Request #{selectedRequest.id}</h2>
              <button onClick={() => setSelectedRequest(null)} className="close-btn">×</button>
            </div>

            <div className="modal-body">
              <div className="request-info">
                <p><strong>User ID:</strong> {selectedRequest.userId}</p>
                <p><strong>Pet ID:</strong> {selectedRequest.petId}</p>
                <p><strong>Status:</strong> <span className={`status-badge ${selectedRequest.status.toLowerCase()}`}>
                  {selectedRequest.status}
                </span></p>
                <p><strong>Submitted:</strong> {new Date(selectedRequest.requestDate).toLocaleString()}</p>

                {selectedRequest.responseDate && (
                  <p><strong>Responded:</strong> {new Date(selectedRequest.responseDate).toLocaleString()}</p>
                )}
              </div>

              <div className="reason-section">
                <h3>Reason for Adoption</h3>
                <p>{selectedRequest.reason}</p>
              </div>

              {selectedRequest.adminNotes && (
                <div className="admin-notes-section">
                  <h3>Admin Notes</h3>
                  <p>{selectedRequest.adminNotes}</p>
                </div>
              )}

              {selectedRequest.status === 'PENDING' && (
                <>
                  <div className="form-group">
                    <label>Admin Notes (Optional)</label>
                    <textarea
                      value={adminNotes}
                      onChange={(e) => setAdminNotes(e.target.value)}
                      rows="3"
                      placeholder="Add any notes about this decision..."
                    />
                  </div>

                  <div className="modal-actions">
                    <button
                      onClick={() => handleApprove(selectedRequest.id)}
                      className="approve-btn"
                    >
                      Approve Request
                    </button>
                    <button
                      onClick={() => handleReject(selectedRequest.id)}
                      className="reject-btn"
                    >
                      Reject Request
                    </button>
                  </div>
                </>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default ManageRequests
