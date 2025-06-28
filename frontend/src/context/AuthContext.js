import React, { createContext, useContext, useEffect, useState } from 'react';
import authService from '../services/authService';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check if user is already logged in
    const currentUser = authService.getCurrentUser();
    const token = authService.getToken();
    
    if (currentUser && token) {
      setUser(currentUser);
    }
    
    setLoading(false);
  }, []);

  const login = async (credentials) => {
    setLoading(true);
    try {
      const response = await authService.login(credentials);
      const userData = {
        username: response.username,
        email: response.email,
        roles: response.roles,
        authorities: response.authorities,
      };
      setUser(userData);
    } catch (error) {
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    authService.logout();
    setUser(null);
  };

  const isAuthenticated = !!user && authService.isAuthenticated();

  const hasAuthority = (authority) => {
    return user?.authorities.includes(authority) || false;
  };

  const hasRole = (role) => {
    return user?.roles.includes(role) || false;
  };

  const value = {
    user,
    loading,
    login,
    logout,
    isAuthenticated,
    hasAuthority,
    hasRole,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
