import { useState, useEffect } from 'react'
import api from '../../services/api'
import './Admin.css'

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

// Breed options for each pet type
const BREED_OPTIONS = {
  DOG: ['Golden Retriever', 'German Shepherd', 'Labrador', 'Bulldog', 'Beagle', 'Poodle', 'Husky', 'Mixed Breed'],
  CAT: ['Persian', 'Siamese', 'Maine Coon', 'British Shorthair', 'Ragdoll', 'Bengal', 'Mixed Breed'],
  BIRD: ['Parrot', 'Canary', 'Budgie', 'Cockatiel', 'Macaw', 'Finch'],
  RABBIT: ['Dutch', 'Lionhead', 'Flemish Giant', 'Mini Lop', 'Mixed Breed'],
  HAMSTER: ['Syrian Hamster', 'Dwarf Hamster', 'Roborovski', 'Chinese Hamster'],
  FISH: ['Goldfish', 'Betta', 'Guppy', 'Angelfish', 'Molly', 'Tetra'],
  OTHER: ['Mixed Breed', 'Unknown']
}

// Health status options
const HEALTH_STATUS_OPTIONS = [
  'Healthy',
  'Vaccinated',
  'Vaccinated, healthy',
  'Vaccinated, neutered, healthy',
  'Vaccinated, spayed, healthy',
  'Vaccinated, neutered, trained',
  'Healthy, active',
  'Needs medication',
  'Recovering'
]

// Available local images - using relative paths from src/assets
const AVAILABLE_IMAGES = {
  DOG: Array.from({length: 11}, (_, i) => `dog${i + 1}.png`),
  CAT: Array.from({length: 10}, (_, i) => `cat${i + 1}.png`),
  BIRD: Array.from({length: 6}, (_, i) => `parrot${i + 1}.png`),
  RABBIT: [],
  HAMSTER: Array.from({length: 5}, (_, i) => `ham${i + 1}.png`),
  FISH: Array.from({length: 5}, (_, i) => `fish${i + 1}.png`),
  OTHER: []
}

function ManagePets() {
  const [pets, setPets] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showAddForm, setShowAddForm] = useState(false)
  const [editingPet, setEditingPet] = useState(null)
  const [formData, setFormData] = useState({
    name: '',
    type: 'DOG',
    breed: '',
    age: '',
    gender: 'MALE',
    healthStatus: 'Healthy',
    description: '',
    imageUrl: '',
    status: 'AVAILABLE'
  })

  useEffect(() => {
    fetchPets()
  }, [])

  const fetchPets = async () => {
    try {
      const response = await api.getAllPets()
      if (response.success) {
        setPets(response.data)
      }
    } catch (err) {
      setError('Failed to load pets')
    } finally {
      setLoading(false)
    }
  }

  const handleChange = (e) => {
    const { name, value } = e.target

    // If type changes, reset breed and imageUrl
    if (name === 'type') {
      setFormData({
        ...formData,
        type: value,
        breed: '',
        imageUrl: ''
      })
    } else {
      setFormData({
        ...formData,
        [name]: value
      })
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    try {
      const petData = {
        ...formData,
        age: parseInt(formData.age)
      }

      let response
      if (editingPet) {
        response = await api.updatePet(editingPet.id, petData)
      } else {
        response = await api.addPet(petData)
      }

      if (response.success) {
        fetchPets()
        resetForm()
        alert(editingPet ? 'Pet updated successfully!' : 'Pet added successfully!')
      } else {
        setError(response.message)
      }
    } catch (err) {
      setError('An error occurred. Please try again.')
    }
  }

  const handleEdit = (pet) => {
    setEditingPet(pet)
    setFormData({
      name: pet.name,
      type: pet.type,
      breed: pet.breed || '',
      age: pet.age.toString(),
      gender: pet.gender,
      healthStatus: pet.healthStatus || 'Healthy',
      description: pet.description || '',
      imageUrl: pet.imageUrl || '',
      status: pet.status || 'AVAILABLE'
    })
    setShowAddForm(true)
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this pet?')) {
      return
    }

    try {
      const response = await api.deletePet(id)
      if (response.success) {
        fetchPets()
        alert('Pet deleted successfully!')
      }
    } catch (err) {
      setError('Failed to delete pet')
    }
  }

  const resetForm = () => {
    setFormData({
      name: '',
      type: 'DOG',
      breed: '',
      age: '',
      gender: 'MALE',
      healthStatus: 'Healthy',
      description: '',
      imageUrl: '',
      status: 'AVAILABLE'
    })
    setEditingPet(null)
    setShowAddForm(false)
  }

  if (loading) {
    return <div className="loading">Loading pets...</div>
  }

  return (
    <div className="manage-pets-container">
      <div className="manage-header">
        <h1>Manage Pets</h1>
        <button
          onClick={() => setShowAddForm(!showAddForm)}
          className="add-pet-btn"
        >
          {showAddForm ? 'Cancel' : '+ Add New Pet'}
        </button>
      </div>

      {error && <div className="error-message">{error}</div>}

      {showAddForm && (
        <div className="pet-form-card">
          <h2>{editingPet ? 'Edit Pet' : 'Add New Pet'}</h2>
          <form onSubmit={handleSubmit} className="pet-form">
            <div className="form-row">
              <div className="form-group">
                <label>Pet Name *</label>
                <input
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="form-group">
                <label>Type *</label>
                <select name="type" value={formData.type} onChange={handleChange} required>
                  <option value="DOG">Dog</option>
                  <option value="CAT">Cat</option>
                  <option value="BIRD">Bird</option>
                  <option value="RABBIT">Rabbit</option>
                  <option value="HAMSTER">Hamster</option>
                  <option value="FISH">Fish</option>
                  <option value="OTHER">Other</option>
                </select>
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Breed *</label>
                <select
                  name="breed"
                  value={formData.breed}
                  onChange={handleChange}
                  required
                >
                  <option value="">Select breed</option>
                  {BREED_OPTIONS[formData.type].map(breed => (
                    <option key={breed} value={breed}>{breed}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label>Age *</label>
                <input
                  type="number"
                  name="age"
                  value={formData.age}
                  onChange={handleChange}
                  min="0"
                  required
                />
              </div>

              <div className="form-group">
                <label>Gender *</label>
                <select name="gender" value={formData.gender} onChange={handleChange} required>
                  <option value="MALE">Male</option>
                  <option value="FEMALE">Female</option>
                  <option value="UNKNOWN">Unknown</option>
                </select>
              </div>
            </div>

            <div className="form-group">
              <label>Health Status *</label>
              <select
                name="healthStatus"
                value={formData.healthStatus}
                onChange={handleChange}
                required
              >
                {HEALTH_STATUS_OPTIONS.map(status => (
                  <option key={status} value={status}>{status}</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Description</label>
              <textarea
                name="description"
                value={formData.description}
                onChange={handleChange}
                rows="4"
                placeholder="Describe the pet's personality, behavior, etc."
              />
            </div>

            <div className="form-group">
              <label>Pet Image *</label>
              <select
                name="imageUrl"
                value={formData.imageUrl}
                onChange={handleChange}
                required
              >
                <option value="">Select an image</option>
                {AVAILABLE_IMAGES[formData.type].map(imageUrl => (
                  <option key={imageUrl} value={imageUrl}>{imageUrl}</option>
                ))}
              </select>
              {formData.imageUrl && (
                <div className="image-preview">
                  <img src={getImagePath(formData.imageUrl)} alt="Preview" style={{width: '100px', marginTop: '10px', borderRadius: '4px'}} />
                </div>
              )}
            </div>

            <div className="form-buttons">
              <button type="submit" className="submit-btn">
                {editingPet ? 'Update Pet' : 'Add Pet'}
              </button>
              <button type="button" onClick={resetForm} className="cancel-btn">
                Cancel
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="pets-table">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Type</th>
              <th>Breed</th>
              <th>Age</th>
              <th>Gender</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {pets.map(pet => (
              <tr key={pet.id}>
                <td>{pet.id}</td>
                <td>{pet.name}</td>
                <td>{pet.type}</td>
                <td>{pet.breed || '-'}</td>
                <td>{pet.age}</td>
                <td>{pet.gender}</td>
                <td>
                  <span className={`status-badge ${pet.status.toLowerCase()}`}>
                    {pet.status}
                  </span>
                </td>
                <td>
                  <button onClick={() => handleEdit(pet)} className="edit-btn">
                    Edit
                  </button>
                  <button onClick={() => handleDelete(pet.id)} className="delete-btn">
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

export default ManagePets
