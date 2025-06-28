const API_BASE_URL = 'http://localhost:8080/api';

class AuthService {
  constructor() {
    this.baseUrl = `${API_BASE_URL}/auth`;
  }

  async login(credentials) {
    const response = await fetch(`${this.baseUrl}/signin`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(credentials),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.error || 'Login failed');
    }

    const data = await response.json();
    
    // Store token and user info
    localStorage.setItem('token', data.token);
    localStorage.setItem('user', JSON.stringify({
      username: data.username,
      email: data.email,
      roles: data.roles,
      authorities: data.authorities,
    }));

    return data;
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  getToken() {
    return localStorage.getItem('token');
  }

  getCurrentUser() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  }

  isAuthenticated() {
    const token = this.getToken();
    const user = this.getCurrentUser();
    return !!(token && user);
  }

  hasAuthority(authority) {
    const user = this.getCurrentUser();
    return user?.authorities.includes(authority) || false;
  }

  hasRole(role) {
    const user = this.getCurrentUser();
    return user?.roles.includes(role) || false;
  }

  getAuthHeaders() {
    const token = this.getToken();
    return token ? { Authorization: `Bearer ${token}` } : {};
  }

  async forgotPassword(username) {
    const response = await fetch(`${this.baseUrl}/forgot-password`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ username }),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.error || 'Password reset request failed');
    }

    return response.json();
  }
}

const authService = new AuthService();
export default authService;
