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
  const [authChecked, setAuthChecked] = useState(false);

  useEffect(() => {
    const initializeAuth = async () => {
      try {
        setLoading(true);
        console.log('Initializing auth, asgardeoIsAuthenticated:', asgardeoIsAuthenticated);
        
        if (asgardeoIsAuthenticated) {
          // Get user info from WSO2 IS
          const basicUserInfo = await getBasicUserInfo();
          const decodedToken = await getDecodedIDToken();
          
          console.log('Basic user info:', basicUserInfo);
          console.log('Decoded token:', decodedToken);
          
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
          
          console.log('Setting user data:', userData);
          setUser(userData);
        } else {
          console.log('Not authenticated, clearing user');
          setUser(null);
        }
      } catch (error) {
        console.error('Error initializing auth:', error);
        setUser(null);
      } finally {
        setLoading(false);
        setAuthChecked(true);
      }
    };

    // Only initialize when Asgardeo SDK is not loading
    if (!isLoading) {
      initializeAuth();
    }
  }, [asgardeoIsAuthenticated, isLoading, getBasicUserInfo, getDecodedIDToken]);

  // Map WSO2 IS groups to TRA roles
  const mapGroupsToRoles = (groups) => {
    console.log('Mapping groups to roles:', groups);
    const roleMapping = {
      'admin': 'ADMIN',
      'customs_officer': 'CUSTOMS_OFFICER',
      'cargo_inspector': 'CARGO_INSPECTOR',
      'vehicle_inspector': 'VEHICLE_INSPECTOR',
      'duty_officer': 'DUTY_OFFICER',
      // Add more mappings as needed
    };

    const mappedRoles = groups.map(group => roleMapping[group.toLowerCase()] || group).filter(Boolean);
    console.log('Mapped roles:', mappedRoles);
    
    // If no roles found, assign a default role
    if (mappedRoles.length === 0) {
      mappedRoles.push('CUSTOMS_OFFICER'); // Default role
    }
    
    return mappedRoles;
  };

  // Map roles to authorities
  const mapRolesToAuthorities = (roles) => {
    console.log('Mapping roles to authorities:', roles);
    const authorityMapping = {
      'admin': ['READ', 'WRITE', 'DELETE', 'MANAGE_USERS', 'VIEW_REPORTS'],
      'customs_officer': ['READ', 'WRITE', 'PROCESS_CLEARANCE', 'VIEW_REPORTS'],
      'cargo_inspector': ['READ', 'WRITE', 'INSPECT_CARGO'],
      'vehicle_inspector': ['READ', 'WRITE', 'INSPECT_VEHICLE'],
      'duty_officer': ['READ', 'WRITE', 'CALCULATE_DUTY', 'PROCESS_PAYMENT']
    };

    const authorities = new Set();
    roles.forEach(role => {
      const roleAuthorities = authorityMapping[role] || ['READ']; // Default to READ access
      roleAuthorities.forEach(auth => authorities.add(auth));
    });

    const mappedAuthorities = Array.from(authorities);
    console.log('Mapped authorities:', mappedAuthorities);
    return mappedAuthorities;
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
    authChecked,
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
