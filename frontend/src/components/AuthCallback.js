import React, { useEffect } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/WSO2AuthContext';

const AuthCallback = () => {
  const { isAuthenticated, loading, user, authChecked } = useAuth();
  const location = useLocation();

  useEffect(() => {
    console.log('AuthCallback - Current state:', { 
      isAuthenticated, 
      loading, 
      user, 
      authChecked,
      pathname: location.pathname,
      search: location.search 
    });
  }, [isAuthenticated, loading, user, authChecked, location]);

  // Show loading while processing callback
  if (loading || !authChecked) {
    return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
        backgroundColor: 'var(--customs-blue)'
      }}>
        <div style={{
          color: 'var(--customs-white)',
          fontSize: 'var(--font-size-large)',
          textAlign: 'center'
        }}>
          <div style={{ marginBottom: 'var(--spacing-lg)' }}>🔄</div>
          <div>Processing login...</div>
        </div>
      </div>
    );
  }

  // If authenticated after callback, redirect to dashboard
  if (isAuthenticated && user) {
    console.log('Auth callback successful, redirecting to dashboard');
    return <Navigate to="/dashboard" replace />;
  }

  // If not authenticated, redirect to login
  console.log('Auth callback failed, redirecting to login');
  return <Navigate to="/login" replace />;
};

export default AuthCallback;
