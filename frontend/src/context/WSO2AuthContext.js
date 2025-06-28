import React, { createContext, useContext, useEffect, useState } from 'react';
import { AuthProvider as AsgardeoAuthProvider, useAuthContext } from "@asgardeo/auth-react";
import authConfig from '../config/authConfig';

const AuthContext = createContext();

// WSO2 Auth Provider Component
export const WSO2AuthProvider = ({ children }) => {
  return (
    <AsgardeoAuthProvider config={authConfig}>
      <AuthProviderWrapper>
        {children}
      </AuthProviderWrapper>
    </AsgardeoAuthProvider>
  );
};

// Wrapper component to handle the auth logic
const AuthProviderWrapper = ({ children }) => {
  const {
    signIn,
    signOut,
    getBasicUserInfo,
    getIDToken,
    getDecodedIDToken,
    getAccessToken,
    refreshAccessToken,
    isAuthenticated: asgardeoIsAuthenticated,
    isLoading
  } = useAuthContext();

  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initializeAuth = async () => {
      try {
        if (asgardeoIsAuthenticated) {
          // Get user info from WSO2 IS
          const basicUserInfo = await getBasicUserInfo();
          const decodedToken = await getDecodedIDToken();
          
          // Map WSO2 IS user data to TRA user format
          const userData = {
            username: basicUserInfo.username || basicUserInfo.preferred_username,
            email: basicUserInfo.email,
            firstName: basicUserInfo.given_name,
            lastName: basicUserInfo.family_name,
            // Map groups to roles (customize based on your WSO2 IS configuration)
            roles: mapGroupsToRoles(decodedToken.groups || []),
            authorities: mapRolesToAuthorities(decodedToken.roles || decodedToken.groups || []),
            // Store additional claims
            claims: decodedToken
          };
          
          setUser(userData);
        } else {
          setUser(null);
        }
      } catch (error) {
        console.error('Error initializing auth:', error);
        setUser(null);
      } finally {
        setLoading(false);
      }
    };

    if (!isLoading) {
      initializeAuth();
    }
  }, [asgardeoIsAuthenticated, isLoading, getBasicUserInfo, getDecodedIDToken]);

  // Map WSO2 IS groups to TRA roles
  const mapGroupsToRoles = (groups) => {
    const roleMapping = {
      'admin': 'ADMIN',
      'customs_officer': 'CUSTOMS_OFFICER',
      'cargo_inspector': 'CARGO_INSPECTOR',
      'vehicle_inspector': 'VEHICLE_INSPECTOR',
      'duty_officer': 'DUTY_OFFICER',
      // Add more mappings as needed
    };

    return groups.map(group => roleMapping[group.toLowerCase()] || group).filter(Boolean);
  };

  // Map roles to authorities
  const mapRolesToAuthorities = (roles) => {
    const authorityMapping = {
      'ADMIN': ['READ', 'WRITE', 'DELETE', 'MANAGE_USERS', 'VIEW_REPORTS'],
      'CUSTOMS_OFFICER': ['READ', 'WRITE', 'PROCESS_CLEARANCE', 'VIEW_REPORTS'],
      'CARGO_INSPECTOR': ['READ', 'WRITE', 'INSPECT_CARGO'],
      'VEHICLE_INSPECTOR': ['READ', 'WRITE', 'INSPECT_VEHICLE'],
      'DUTY_OFFICER': ['READ', 'WRITE', 'CALCULATE_DUTY', 'PROCESS_PAYMENT']
    };

    const authorities = new Set();
    roles.forEach(role => {
      const roleAuthorities = authorityMapping[role] || [];
      roleAuthorities.forEach(auth => authorities.add(auth));
    });

    return Array.from(authorities);
  };

  const login = async () => {
    try {
      await signIn();
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  };

  const logout = async () => {
    try {
      await signOut();
      setUser(null);
    } catch (error) {
      console.error('Logout error:', error);
      // Force local logout even if remote logout fails
      setUser(null);
    }
  };

  const isAuthenticated = asgardeoIsAuthenticated && !!user;

  const hasAuthority = (authority) => {
    return user?.authorities?.includes(authority) || false;
  };

  const hasRole = (role) => {
    return user?.roles?.includes(role) || false;
  };

  const getToken = async () => {
    try {
      return await getAccessToken();
    } catch (error) {
      console.error('Error getting access token:', error);
      return null;
    }
  };

  const refreshToken = async () => {
    try {
      await refreshAccessToken();
    } catch (error) {
      console.error('Error refreshing token:', error);
      throw error;
    }
  };

  const value = {
    user,
    loading: loading || isLoading,
    login,
    logout,
    isAuthenticated,
    hasAuthority,
    hasRole,
    getToken,
    refreshToken,
    // Expose additional WSO2 specific methods
    getIDToken,
    getDecodedIDToken,
    getBasicUserInfo
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within a WSO2AuthProvider');
  }
  return context;
};
