import { useAuthenticatedAPI } from './wso2AuthService';
import { useMemo } from 'react';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';

// Custom hook for cargo operations
export const useCargoService = () => {
  const api = useAuthenticatedAPI();

  return useMemo(() => ({
    getAllCargo: async (page = 0, size = 10, sortBy = 'createdAt', sortDir = 'desc', status = undefined) => {
      try {
        let url = `${API_BASE_URL}/cargo?page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`;
        if (status) {
          url += `&status=${status}`;
        }
        const response = await api.get(url);
        return await response.json();
      } catch (error) {
        console.error('Error fetching cargo:', error);
        throw error;
      }
    },

    getCargoById: async (id) => {
      try {
        const response = await api.get(`${API_BASE_URL}/cargo/${id}`);
        return await response.json();
      } catch (error) {
        console.error('Error fetching cargo by ID:', error);
        throw error;
      }
    },

    createCargo: async (cargoData) => {
      try {
        const response = await api.post(`${API_BASE_URL}/cargo`, cargoData);
        return await response.json();
      } catch (error) {
        console.error('Error creating cargo:', error);
        throw error;
      }
    },

    updateCargo: async (id, cargoData) => {
      try {
        const response = await api.put(`${API_BASE_URL}/cargo/${id}`, cargoData);
        return await response.json();
      } catch (error) {
        console.error('Error updating cargo:', error);
        throw error;
      }
    },

    deleteCargo: async (id) => {
      try {
        await api.delete(`${API_BASE_URL}/cargo/${id}`);
      } catch (error) {
        console.error('Error deleting cargo:', error);
        throw error;
      }
    },

    updateCargoStatus: async (id, status) => {
      try {
        const response = await api.put(`${API_BASE_URL}/cargo/${id}/status`, { status });
        return await response.json();
      } catch (error) {
        console.error('Error updating cargo status:', error);
        throw error;
      }
    },

    searchCargo: async (searchParams) => {
      try {
        const queryString = new URLSearchParams(searchParams).toString();
        const response = await api.get(`${API_BASE_URL}/cargo/search?${queryString}`);
        return await response.json();
      } catch (error) {
        console.error('Error searching cargo:', error);
        throw error;
      }
    }
  }), [api]);
};

// Legacy class for backward compatibility during transition
class CargoService {
  constructor() {
    this.baseUrl = `${API_BASE_URL}/cargo`;
  }

  // Note: These methods will need auth headers to be passed manually
  // Recommend using the useCargoService hook instead

  async getAllCargo(authHeaders = {}) {
    const response = await fetch(this.baseUrl, {
      headers: {
        'Content-Type': 'application/json',
        ...authHeaders
      }
    });

    if (!response.ok) {
      throw new Error('Failed to fetch cargo');
    }

    return response.json();
  }

  async getCargoById(id, authHeaders = {}) {
    const response = await fetch(`${this.baseUrl}/${id}`, {
      headers: {
        'Content-Type': 'application/json',
        ...authHeaders
      }
    });

    if (!response.ok) {
      throw new Error('Failed to fetch cargo');
    }

    return response.json();
  }

  async createCargo(cargoData, authHeaders = {}) {
    const response = await fetch(this.baseUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...authHeaders
      },
      body: JSON.stringify(cargoData)
    });

    if (!response.ok) {
      throw new Error('Failed to create cargo');
    }

    return response.json();
  }

  async updateCargo(id, cargoData, authHeaders = {}) {
    const response = await fetch(`${this.baseUrl}/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        ...authHeaders
      },
      body: JSON.stringify(cargoData)
    });

    if (!response.ok) {
      throw new Error('Failed to update cargo');
    }

    return response.json();
  }

  async deleteCargo(id, authHeaders = {}) {
    const response = await fetch(`${this.baseUrl}/${id}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        ...authHeaders
      }
    });

    if (!response.ok) {
      throw new Error('Failed to delete cargo');
    }
  }
}

const cargoService = new CargoService();
export default cargoService;
