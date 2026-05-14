const API_BASE_URL = 'http://localhost:8082/api'

const api = {
  // Auth endpoints
  register: async (userData) => {
    const response = await fetch(`${API_BASE_URL}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(userData)
    })
    return response.json()
  },

  login: async (credentials) => {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(credentials)
    })
    return response.json()
  },

  // Pet endpoints
  getAllPets: async () => {
    const response = await fetch(`${API_BASE_URL}/pets`)
    return response.json()
  },

  // Get ALL pets including PENDING and ADOPTED (for admin)
  getAllPetsAdmin: async () => {
    const response = await fetch(`${API_BASE_URL}/pets/filter`)
    return response.json()
  },

  getPetById: async (id) => {
    const response = await fetch(`${API_BASE_URL}/pets/${id}`)
    return response.json()
  },

  addPet: async (petData) => {
    const response = await fetch(`${API_BASE_URL}/pets`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(petData)
    })
    return response.json()
  },

  updatePet: async (id, petData) => {
    const response = await fetch(`${API_BASE_URL}/pets/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(petData)
    })
    return response.json()
  },

  deletePet: async (id) => {
    const response = await fetch(`${API_BASE_URL}/pets/${id}`, {
      method: 'DELETE'
    })
    return response.json()
  },

  // Adoption endpoints
  submitAdoptionRequest: async (requestData) => {
    const response = await fetch(`${API_BASE_URL}/adoptions`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(requestData)
    })
    return response.json()
  },

  getUserRequests: async (userId) => {
    const response = await fetch(`${API_BASE_URL}/adoptions/my-requests/${userId}`)
    return response.json()
  },

  getPendingRequests: async () => {
    const response = await fetch(`${API_BASE_URL}/adoptions/pending`)
    return response.json()
  },

  getAllRequests: async () => {
    const response = await fetch(`${API_BASE_URL}/adoptions`)
    return response.json()
  },

  approveRequest: async (id, adminNotes) => {
    const response = await fetch(`${API_BASE_URL}/adoptions/${id}/approve`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ adminNotes })
    })
    return response.json()
  },

  rejectRequest: async (id, adminNotes) => {
    const response = await fetch(`${API_BASE_URL}/adoptions/${id}/reject`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ adminNotes })
    })
    return response.json()
  },

  // Notification endpoints
  getUserNotifications: async (userId) => {
    const response = await fetch(`${API_BASE_URL}/notifications/${userId}`)
    return response.json()
  },

  markAsRead: async (id) => {
    const response = await fetch(`${API_BASE_URL}/notifications/${id}/read`, {
      method: 'PUT'
    })
    return response.json()
  }
}

export default api
