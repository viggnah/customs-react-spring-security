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
        background: 'linear-gradient(135deg, var(--tra-black) 0%, var(--tra-dark-gray) 25%, #1a1a2e 50%, #16213e 75%, #0f3460 100%)'
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
      background: 'linear-gradient(135deg, var(--tra-black) 0%, var(--tra-dark-gray) 25%, #1a1a2e 50%, #16213e 75%, #0f3460 100%)',
      position: 'relative',
      overflow: 'hidden'
    }}>
      {/* Background decoration */}
      <div style={{
        position: 'absolute',
        top: '-50%',
        left: '-50%',
        width: '200%',
        height: '200%',
        background: 'radial-gradient(circle, rgba(255,215,0,0.1) 2px, transparent 2px)',
        backgroundSize: '50px 50px',
        animation: 'float 20s ease-in-out infinite'
      }}></div>
      
      <div style={{
        backgroundColor: 'rgba(255, 255, 255, 0.98)',
        backdropFilter: 'blur(15px)',
        padding: '3rem',
        borderRadius: '20px',
        boxShadow: '0 25px 50px rgba(0, 0, 0, 0.25), 0 0 0 1px rgba(255, 215, 0, 0.1)',
        width: '100%',
        maxWidth: '450px',
        textAlign: 'center',
        border: '1px solid rgba(255, 215, 0, 0.2)',
        position: 'relative',
        zIndex: 1
      }}>
        {/* TRA Logo and Title */}
        <div style={{ marginBottom: '2.5rem' }}>
          <div style={{
            width: '80px',
            height: '80px',
            background: 'linear-gradient(135deg, var(--tra-yellow) 0%, var(--tra-dark-yellow) 100%)',
            borderRadius: '50%',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            margin: '0 auto 1.5rem',
            boxShadow: '0 10px 25px rgba(255, 215, 0, 0.3)',
            border: '3px solid rgba(255, 215, 0, 0.2)'
          }}>
            <span style={{
              color: 'var(--tra-black)',
              fontSize: '2rem',
              fontWeight: 'bold'
            }}>🏛️</span>
          </div>
          
          <h1 style={{
            color: 'var(--tra-black)',
            fontSize: '2.8rem',
            fontWeight: '800',
            margin: 0,
            marginBottom: '0.5rem',
            letterSpacing: '-1px',
            textShadow: '0 2px 4px rgba(255, 215, 0, 0.5)',
            position: 'relative'
          }}>
            TRA
          </h1>
          <h2 style={{
            color: 'var(--tra-gray)',
            fontSize: '1.25rem',
            fontWeight: '600',
            margin: 0,
            marginBottom: '0.5rem'
          }}>
            Customs Management System
          </h2>
          <p style={{
            color: 'var(--tra-light-gray)',
            fontSize: '0.95rem',
            margin: 0,
            fontWeight: '500'
          }}>
            Secure login with WSO2 Identity Server
          </p>
        </div>

        {/* Login Form */}
        <div style={{ marginBottom: '2rem' }}>
          <div style={{
            background: 'linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%)',
            padding: '1.5rem',
            borderRadius: '12px',
            marginBottom: '2rem',
            border: '1px solid var(--tra-border-gray)',
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.1)'
          }}>
            <div style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              marginBottom: '1rem'
            }}>
              <span style={{ 
                fontSize: '2rem', 
                marginRight: '0.5rem',
                filter: 'drop-shadow(0 2px 4px rgba(255, 215, 0, 0.3))'
              }}>🔒</span>
            </div>
            <p style={{
              margin: 0,
              color: 'var(--tra-gray)',
              fontSize: '0.95rem',
              lineHeight: '1.6',
              fontWeight: '500'
            }}>
              Click the button below to sign in securely using your organization credentials through WSO2 Identity Server.
            </p>
          </div>

          <button
            onClick={handleLogin}
            disabled={isLoggingIn}
            style={{
              width: '100%',
              padding: '1rem 1.5rem',
              background: isLoggingIn 
                ? 'linear-gradient(135deg, var(--tra-light-gray) 0%, var(--tra-gray) 100%)'
                : 'linear-gradient(135deg, var(--tra-yellow) 0%, var(--tra-dark-yellow) 100%)',
              color: isLoggingIn ? 'var(--tra-white)' : 'var(--tra-black)',
              border: 'none',
              borderRadius: '12px',
              fontSize: '1.1rem',
              fontWeight: '700',
              cursor: isLoggingIn ? 'not-allowed' : 'pointer',
              transition: 'all 0.3s ease',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '0.75rem',
              boxShadow: isLoggingIn 
                ? '0 4px 15px rgba(102, 102, 102, 0.4)'
                : '0 8px 25px rgba(255, 215, 0, 0.4)',
              transform: 'translateY(0)',
              position: 'relative',
              overflow: 'hidden'
            }}
            onMouseEnter={(e) => {
              if (!isLoggingIn) {
                e.target.style.transform = 'translateY(-2px)';
                e.target.style.boxShadow = '0 12px 35px rgba(255, 215, 0, 0.6)';
                e.target.style.background = 'linear-gradient(135deg, var(--tra-dark-yellow) 0%, #ccac00 100%)';
              }
            }}
            onMouseLeave={(e) => {
              if (!isLoggingIn) {
                e.target.style.transform = 'translateY(0)';
                e.target.style.boxShadow = '0 8px 25px rgba(255, 215, 0, 0.4)';
                e.target.style.background = 'linear-gradient(135deg, var(--tra-yellow) 0%, var(--tra-dark-yellow) 100%)';
              }
            }}
          >
            {isLoggingIn ? (
              <>
                <div style={{
                  width: '20px',
                  height: '20px',
                  border: '2px solid rgba(0,0,0,0.3)',
                  borderTop: '2px solid var(--tra-black)',
                  borderRadius: '50%',
                  animation: 'spin 1s linear infinite'
                }}></div>
                <span>Signing in...</span>
              </>
            ) : (
              <>
                <span style={{ fontSize: '1.2rem' }}>🔐</span>
                <span>Sign in with WSO2</span>
              </>
            )}
          </button>
        </div>

        {/* Error Message */}
        {loginError && (
          <div style={{
            background: 'linear-gradient(135deg, #ffebee 0%, #ffcdd2 100%)',
            color: 'var(--danger-color)',
            padding: '1rem',
            borderRadius: '12px',
            border: '1px solid #ffcdd2',
            fontSize: '0.9rem',
            marginBottom: '1.5rem',
            display: 'flex',
            alignItems: 'center',
            gap: '0.5rem'
          }}>
            <span>⚠️</span>
            <span>{loginError}</span>
          </div>
        )}

        {/* Footer */}
        <div style={{
          borderTop: '1px solid var(--tra-border-gray)',
          paddingTop: '1.5rem',
          marginTop: '2rem'
        }}>
          <p style={{
            margin: 0,
            color: 'var(--tra-gray)',
            fontSize: '1rem',
            fontWeight: '600'
          }}>
            Tanzania Revenue Authority
          </p>
          <p style={{
            margin: 0,
            color: 'var(--tra-light-gray)',
            fontSize: '0.85rem',
            marginTop: '0.5rem',
            fontWeight: '500'
          }}>
          </p>
        </div>
      </div>

      {/* Add CSS animations */}
      <style jsx>{`
        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }
        
        @keyframes float {
          0%, 100% { transform: translateY(0px) rotate(0deg); }
          50% { transform: translateY(-20px) rotate(180deg); }
        }
      `}</style>
    </div>
  );
};

export default WSO2Login;
