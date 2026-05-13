import { useState, useEffect } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import api from '../../services/api'
import './Adopter.css'

// Helper function to get image path
const getImagePath = (imageUrl) => {
  if (!imageUrl) return null
  try {
    return new URL(`../../assets/${imageUrl}`, import.meta.url).href
  } catch (e) {
    console.error('Error loading image:', imageUrl, e)
    return null
  }
}

function PetDetails({ user }) {
  const { id } = useParams()
  const navigate = useNavigate()
  const [pet, setPet] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showAdoptForm, setShowAdoptForm] = useState(false)
  const [reason, setReason] = useState('')
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    fetchPet()
  }, [id])

  const fetchPet = async () => {
    try {
      const response = await api.getPetById(id)
      if (response.success) {
        setPet(response.data)
      } else {
        setError('Pet not found')
      }
    } catch (err) {
      setError('An error occurred while loading pet details')
    } finally {
      setLoading(false)
    }
  }

  const handleAdoptSubmit = async (e) => {
    e.preventDefault()

    if (!user) {
      navigate('/login')
      return
    }

    if (reason.length < 10) {
      setError('Please provide at least 10 characters explaining why you want to adopt')
      return
    }

    setSubmitting(true)
    setError('')

    try {
      const response = await api.submitAdoptionRequest({
        userId: user.userId,
        petId: pet.id,
        reason
      })

      if (response.success) {
        alert('Adoption request submitted successfully!')
        navigate('/my-requests')
      } else {
        setError(response.message || 'Failed to submit adoption request')
      }
    } catch (err) {
      setError('An error occurred. Please try again.')
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return <div className="loading">Loading pet details...</div>
  }

  if (error && !pet) {
    return <div className="error-message">{error}</div>
  }

  return (
    <div className="pet-details-container">
      <Link to="/" className="back-link">← Back to all pets</Link>

      <div className="pet-details-card">
        <div className="pet-details-image">
          {pet.imageUrl && getImagePath(pet.imageUrl) ? (
            <img src={getImagePath(pet.imageUrl)} alt={pet.name} />
          ) : (
            <div className="pet-details-placeholder">
              {pet.type === 'DOG' && '🐕'}
              {pet.type === 'CAT' && '🐈'}
              {pet.type === 'BIRD' && '🐦'}
              {pet.type === 'RABBIT' && '🐰'}
              {pet.type === 'HAMSTER' && '🐹'}
              {pet.type === 'FISH' && '🐠'}
              {pet.type === 'OTHER' && '🐾'}
            </div>
          )}
        </div>

        <div className="pet-details-info">
          <h1>{pet.name}</h1>

          <div className="pet-details-meta">
            <span className="meta-item"><strong>Type:</strong> {pet.type}</span>
            {pet.breed && <span className="meta-item"><strong>Breed:</strong> {pet.breed}</span>}
            <span className="meta-item"><strong>Age:</strong> {pet.age} {pet.age === 1 ? 'year' : 'years'}</span>
            <span className="meta-item"><strong>Gender:</strong> {pet.gender}</span>
          </div>

          <div className="pet-details-section">
            <h3>Health Status</h3>
            <p>{pet.healthStatus || 'Not specified'}</p>
          </div>

          <div className="pet-details-section">
            <h3>About {pet.name}</h3>
            <p>{pet.description || 'No description available'}</p>
          </div>

          <div className="pet-status-section">
            <span className={`pet-status-badge ${pet.status.toLowerCase()}`}>
              {pet.status}
            </span>
          </div>

          {error && <div className="error-message">{error}</div>}

          {pet.status === 'AVAILABLE' && user?.role === 'ADOPTER' && !showAdoptForm && (
            <button onClick={() => setShowAdoptForm(true)} className="adopt-btn">
              Adopt {pet.name}
            </button>
          )}

          {pet.status === 'PENDING' && (
            <div className="info-message">This pet has a pending adoption request</div>
          )}

          {pet.status === 'ADOPTED' && (
            <div className="info-message">{pet.name} has been adopted</div>
          )}

          {!user && pet.status === 'AVAILABLE' && (
            <div className="info-message">
              Please <Link to="/login">login</Link> to submit an adoption request
            </div>
          )}

          {showAdoptForm && (
            <div className="adopt-form">
              <h3>Adoption Request Form</h3>
              <form onSubmit={handleAdoptSubmit}>
                <div className="form-group">
                  <label>Why do you want to adopt {pet.name}?</label>
                  <textarea
                    value={reason}
                    onChange={(e) => setReason(e.target.value)}
                    required
                    minLength="10"
                    rows="5"
                    placeholder="Tell us why you would be a great adopter for this pet (minimum 10 characters)"
                  />
                </div>

                <div className="form-buttons">
                  <button type="submit" className="submit-btn" disabled={submitting}>
                    {submitting ? 'Submitting...' : 'Submit Request'}
                  </button>
                  <button
                    type="button"
                    onClick={() => setShowAdoptForm(false)}
                    className="cancel-btn"
                  >
                    Cancel
                  </button>
                </div>
              </form>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

export default PetDetails
