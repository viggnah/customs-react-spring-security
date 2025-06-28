import React, { useEffect, useState } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/WSO2AuthContext';

const WSO2Login = () => {
  const { isAuthenticated, loading, login, user, authChecked } = useAuth();
  const [loginError, setLoginError] = useState('');
  const [isLoggingIn, setIsLoggingIn] = useState(false);
  const location = useLocation();

  // Get the intended destination or default to dashboard
  const from = location.state?.from?.pathname || '/dashboard';

  useEffect(() => {
    console.log('WSO2Login - Auth state:', { isAuthenticated, loading, user, authChecked });
  }, [isAuthenticated, loading, user, authChecked]);

  const handleLogin = async () => {
    try {
      setIsLoggingIn(true);
      setLoginError('');
      await login();
    } catch (error) {
      console.error('Login failed:', error);
      setLoginError('Login failed. Please try again.');
    } finally {
      setIsLoggingIn(false);
    }
  };

  // Show loading spinner while checking authentication status
  if (loading || !authChecked) {
    return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
        backgroundColor: 'var(--tra-blue)'
      }}>
        <div style={{
          color: 'var(--tra-white)',
          fontSize: 'var(--font-size-large)',
          textAlign: 'center'
        }}>
          <div style={{ marginBottom: 'var(--spacing-lg)' }}>🔄</div>
          <div>Loading...</div>
        </div>
      </div>
    );
  }

  // If authenticated, redirect to intended destination
  if (isAuthenticated && user) {
    console.log('Redirecting to:', from);
    return <Navigate to={from} replace />;
  }

  return (
    <div style={{
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      height: '100vh',
      backgroundColor: 'var(--tra-blue)',
      backgroundImage: 'linear-gradient(135deg, var(--tra-blue) 0%, var(--tra-dark-blue) 100%)'
    }}>
      <div style={{
        backgroundColor: 'var(--tra-white)',
        padding: 'var(--spacing-xxl)',
        borderRadius: 'var(--border-radius-lg)',
        boxShadow: '0 10px 30px rgba(0, 0, 0, 0.3)',
        width: '100%',
        maxWidth: '400px',
        textAlign: 'center'
      }}>
        {/* TRA Logo and Title */}
        <div style={{ marginBottom: 'var(--spacing-xxl)' }}>
          <h1 style={{
            color: 'var(--tra-blue)',
            fontSize: '2.5rem',
            fontWeight: 'bold',
            margin: 0,
            marginBottom: 'var(--spacing-sm)'
          }}>
            TRA
          </h1>
          <h2 style={{
            color: 'var(--tra-gray)',
            fontSize: 'var(--font-size-large)',
            fontWeight: 'normal',
            margin: 0,
            marginBottom: 'var(--spacing-sm)'
          }}>
            Customs Management System
          </h2>
          <p style={{
            color: 'var(--tra-dark-gray)',
            fontSize: 'var(--font-size-small)',
            margin: 0
          }}>
            Secure login with WSO2 Identity Server
          </p>
        </div>

        {/* Login Form */}
        <div style={{ marginBottom: 'var(--spacing-lg)' }}>
          <div style={{
            backgroundColor: 'var(--tra-light-gray)',
            padding: 'var(--spacing-lg)',
            borderRadius: 'var(--border-radius-md)',
            marginBottom: 'var(--spacing-lg)'
          }}>
            <p style={{
              margin: 0,
              color: 'var(--tra-dark-gray)',
              fontSize: 'var(--font-size-small)',
              lineHeight: '1.5'
            }}>
              Click the button below to sign in securely using your organization credentials through WSO2 Identity Server.
            </p>
          </div>

          <button
            onClick={handleLogin}
            disabled={isLoggingIn}
            style={{
              width: '100%',
              padding: 'var(--spacing-md) var(--spacing-lg)',
              backgroundColor: isLoggingIn ? 'var(--tra-gray)' : 'var(--tra-blue)',
              color: 'var(--tra-white)',
              border: 'none',
              borderRadius: 'var(--border-radius-md)',
              fontSize: 'var(--font-size-medium)',
              fontWeight: 'bold',
              cursor: isLoggingIn ? 'not-allowed' : 'pointer',
              transition: 'all 0.3s ease',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: 'var(--spacing-sm)'
            }}
            onMouseEnter={(e) => {
              if (!isLoggingIn) {
                e.target.style.backgroundColor = 'var(--tra-dark-blue)';
              }
            }}
            onMouseLeave={(e) => {
              if (!isLoggingIn) {
                e.target.style.backgroundColor = 'var(--tra-blue)';
              }
            }}
          >
            {isLoggingIn ? (
              <>
                <span>🔄</span>
                <span>Signing in...</span>
              </>
            ) : (
              <>
                <span>🔐</span>
                <span>Sign in with WSO2</span>
              </>
            )}
          </button>
        </div>

        {/* Error Message */}
        {loginError && (
          <div style={{
            backgroundColor: '#ffebee',
            color: '#c62828',
            padding: 'var(--spacing-md)',
            borderRadius: 'var(--border-radius-md)',
            border: '1px solid #ffcdd2',
            fontSize: 'var(--font-size-small)',
            marginBottom: 'var(--spacing-lg)'
          }}>
            {loginError}
          </div>
        )}

        {/* Footer */}
        <div style={{
          borderTop: '1px solid var(--tra-light-gray)',
          paddingTop: 'var(--spacing-lg)',
          marginTop: 'var(--spacing-lg)'
        }}>
          <p style={{
            margin: 0,
            color: 'var(--tra-dark-gray)',
            fontSize: 'var(--font-size-small)'
          }}>
            Tanzania Revenue Authority
          </p>
          <p style={{
            margin: 0,
            color: 'var(--tra-gray)',
            fontSize: 'var(--font-size-tiny)',
            marginTop: 'var(--spacing-xs)'
          }}>
            Powered by WSO2 Identity Server
          </p>
        </div>
      </div>
    </div>
  );
};

export default WSO2Login;
