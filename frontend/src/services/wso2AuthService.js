import { useAuth } from '../context/WSO2AuthContext';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';

class WSO2AuthService {
  constructor() {
    this.baseUrl = `${API_BASE_URL}/auth`;
  }

  // This method will be used to get auth headers for API calls
  async getAuthHeaders() {
    // Note: This will be used with a hook in components
    // For now, we'll return a placeholder
    return {};
  }

  // Enhanced API call method that handles WSO2 IS tokens
  async makeAuthenticatedRequest(url, options = {}) {
    // This method should be called from components where we have access to the auth context
    const defaultHeaders = {
      'Content-Type': 'application/json',
      ...options.headers
    };

    return fetch(url, {
      ...options,
      headers: defaultHeaders
    });
  }

  // Legacy methods kept for backward compatibility during transition
  getCurrentUser() {
    // This will be deprecated in favor of useAuth hook
    return null;
  }

  getToken() {
    // This will be deprecated in favor of useAuth hook
    return null;
  }

  isAuthenticated() {
    // This will be deprecated in favor of useAuth hook
    return false;
  }

  // Utility method to validate backend response
  async validateWithBackend(accessToken) {
    try {
      const response = await fetch(`${this.baseUrl}/validate`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${accessToken}`
        }
      });

      if (!response.ok) {
        throw new Error('Token validation failed');
      }

      return await response.json();
    } catch (error) {
      console.error('Backend validation error:', error);
      throw error;
    }
  }
}

// Custom hook for making authenticated API calls
export const useAuthenticatedAPI = () => {
  const { getToken, refreshToken, logout } = useAuth();

  const makeRequest = async (url, options = {}) => {
    try {
      // Get current access token
      let accessToken = await getToken();
      
      if (!accessToken) {
        throw new Error('No access token available');
      }

      const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${accessToken}`,
        ...options.headers
      };

      let response = await fetch(url, {
        ...options,
        headers
      });

      // If token expired, try to refresh
      if (response.status === 401) {
        try {
          await refreshToken();
          accessToken = await getToken();
          
          // Retry with new token
          response = await fetch(url, {
            ...options,
            headers: {
              ...headers,
              'Authorization': `Bearer ${accessToken}`
            }
          });
        } catch (refreshError) {
          // Refresh failed, logout user
          console.error('Token refresh failed:', refreshError);
          await logout();
          throw new Error('Session expired. Please login again.');
        }
      }

      if (!response.ok) {
        const error = await response.json().catch(() => ({}));
        throw new Error(error.message || `Request failed: ${response.status}`);
      }

      return response;
    } catch (error) {
      console.error('API request error:', error);
      throw error;
    }
  };

  return {
    makeRequest,
    get: (url, options) => makeRequest(url, { ...options, method: 'GET' }),
    post: (url, data, options) => makeRequest(url, { 
      ...options, 
      method: 'POST', 
      body: JSON.stringify(data) 
    }),
    put: (url, data, options) => makeRequest(url, { 
      ...options, 
      method: 'PUT', 
      body: JSON.stringify(data) 
    }),
    delete: (url, options) => makeRequest(url, { ...options, method: 'DELETE' })
  };
};

const wso2AuthService = new WSO2AuthService();
export default wso2AuthService;
