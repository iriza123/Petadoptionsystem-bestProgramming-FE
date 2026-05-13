import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
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

function Home({ user }) {
  const [pets, setPets] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [filter, setFilter] = useState('ALL')

  useEffect(() => {
    fetchPets()
  }, [])

  const fetchPets = async () => {
    try {
      const response = await api.getAllPets()
      if (response.success) {
        setPets(response.data)
      } else {
        setError('Failed to load pets')
      }
    } catch (err) {
      setError('An error occurred while loading pets')
    } finally {
      setLoading(false)
    }
  }

  const filteredPets = filter === 'ALL'
    ? pets
    : pets.filter(pet => pet.type === filter)

  const displayedPets = filteredPets

  if (loading) {
    return <div className="loading">Loading pets...</div>
  }

  return (
    <div className="home-container">
      <div className="home-header">
        <h1>Available Pets for Adoption</h1>
        <p>Find your perfect companion</p>
      </div>

      {error && <div className="error-message">{error}</div>}

      <div className="filter-section">
        <label>Filter by type: </label>
        <select value={filter} onChange={(e) => setFilter(e.target.value)} className="filter-select">
          <option value="ALL">All Pets</option>
          <option value="DOG">Dogs</option>
          <option value="CAT">Cats</option>
          <option value="BIRD">Birds</option>
          <option value="RABBIT">Rabbits</option>
          <option value="HAMSTER">Hamsters</option>
          <option value="FISH">Fish</option>
          <option value="OTHER">Other</option>
        </select>
      </div>

      {displayedPets.length === 0 ? (
        <div className="no-pets">
          <p>No pets available at the moment. Check back later!</p>
        </div>
      ) : (
        <>
          <div className="pets-grid">
            {displayedPets.map(pet => (
            <div key={pet.id} className="pet-card">
              <div className="pet-image">
                {pet.imageUrl && getImagePath(pet.imageUrl) ? (
                  <img src={getImagePath(pet.imageUrl)} alt={pet.name} />
                ) : (
                  <div className="pet-placeholder">
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

              <div className="pet-info">
                <h3>{pet.name}</h3>
                <p className="pet-type">{pet.type} {pet.breed && `• ${pet.breed}`}</p>
                <p className="pet-age">Age: {pet.age} {pet.age === 1 ? 'year' : 'years'}</p>
                <p className="pet-gender">Gender: {pet.gender}</p>

                <span className={`pet-status ${pet.status.toLowerCase()}`}>
                  {pet.status}
                </span>

                <Link to={`/pets/${pet.id}`} className="view-details-btn">
                  View Details
                </Link>
              </div>
            </div>
          ))}
          </div>
        </>
      )}
    </div>
  )
}

export default Home
