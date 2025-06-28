import authService from './authService';

export const CargoStatus = {
  PENDING_INSPECTION: 'PENDING_INSPECTION',
  UNDER_INSPECTION: 'UNDER_INSPECTION',
  INSPECTION_COMPLETED: 'INSPECTION_COMPLETED',
  CLEARED: 'CLEARED',
  HELD: 'HELD',
  REJECTED: 'REJECTED',
  DUTY_PENDING: 'DUTY_PENDING',
  DUTY_PAID: 'DUTY_PAID'
};

const API_BASE_URL = 'http://localhost:8080/api';

class CargoService {
  constructor() {
    this.baseUrl = `${API_BASE_URL}/cargo`;
  }

  async getAllCargo(page = 0, size = 10, sortBy = 'createdAt', sortDir = 'desc', status) {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      sortBy,
      sortDir,
    });

    if (status) {
      params.append('status', status);
    }

    const response = await fetch(`${this.baseUrl}?${params}`, {
      headers: {
        'Content-Type': 'application/json',
        ...authService.getAuthHeaders(),
      },
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to fetch cargo entries');
    }

    return response.json();
  }

  async getCargoById(id) {
    const response = await fetch(`${this.baseUrl}/${id}`, {
      headers: {
        'Content-Type': 'application/json',
        ...authService.getAuthHeaders(),
      },
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to fetch cargo entry');
    }

    return response.json();
  }

  async createCargo(cargo) {
    const response = await fetch(this.baseUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...authService.getAuthHeaders(),
      },
      body: JSON.stringify(cargo),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to create cargo entry');
    }

    return response.json();
  }

  async updateCargo(id, updates) {
    const response = await fetch(`${this.baseUrl}/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        ...authService.getAuthHeaders(),
      },
      body: JSON.stringify(updates),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to update cargo entry');
    }

    return response.json();
  }

  async deleteCargo(id) {
    const response = await fetch(`${this.baseUrl}/${id}`, {
      method: 'DELETE',
      headers: {
        ...authService.getAuthHeaders(),
      },
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to delete cargo entry');
    }
  }

  async assignInspector(cargoId, inspectorId) {
    const response = await fetch(`${this.baseUrl}/${cargoId}/assign-inspector`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...authService.getAuthHeaders(),
      },
      body: JSON.stringify({ inspectorId }),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to assign inspector');
    }

    return response.json();
  }

  async updateStatus(cargoId, status, remarks) {
    const response = await fetch(`${this.baseUrl}/${cargoId}/status`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        ...authService.getAuthHeaders(),
      },
      body: JSON.stringify({ status, remarks }),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to update status');
    }

    return response.json();
  }
}

const cargoService = new CargoService();
export default cargoService;
