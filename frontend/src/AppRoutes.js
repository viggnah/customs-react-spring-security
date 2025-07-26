import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/WSO2AuthContext';

// Import pages
import WSO2Login from './pages/WSO2Login';
import LoginPage from './pages/LoginPage'; // Keep old login as fallback
import DashboardPage from './pages/DashboardPage';
import CargoPage from './pages/CargoPage';
import VehiclePage from './pages/VehiclePage';
import AdminPage from './pages/AdminPage';
import AuthCallback from './components/AuthCallback';

// Protected Route Component
const ProtectedRoute = ({ children, requiredRoles = [] }) => {
  const { isAuthenticated, user, loading, authChecked } = useAuth();

  // Show loading while checking authentication
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
          <div>Authenticating...</div>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // Check if user has required roles
  if (requiredRoles.length > 0 && user) {
    const hasRequiredRole = requiredRoles.some(role => 
      user.roles?.includes(role)
    );
    
    if (!hasRequiredRole) {
      return <Navigate to="/dashboard" replace />;
    }
  }

  return children;
};

const AppRoutes = () => {
  const { isAuthenticated, loading, authChecked } = useAuth();

  // Show loading while initializing authentication
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
          <div>Loading...</div>
        </div>
      </div>
    );
  }

  return (
    <Routes>
      {/* OAuth2 Callback Route */}
      <Route path="/auth/callback" element={<AuthCallback />} />
      
      {/* Public Routes */}
      <Route 
        path="/login" 
        element={
          isAuthenticated ? 
            <Navigate to="/dashboard" replace /> : 
            <WSO2Login />
        } 
      />
      
      {/* Legacy login route */}
      <Route 
        path="/legacy-login" 
        element={
          isAuthenticated ? 
            <Navigate to="/dashboard" replace /> : 
            <LoginPage />
        } 
      />
      
      {/* Protected Routes */}
      <Route 
        path="/dashboard" 
        element={
          <ProtectedRoute>
            <DashboardPage />
          </ProtectedRoute>
        } 
      />
      
      <Route 
        path="/cargo" 
        element={
          <ProtectedRoute requiredRoles={['ADMIN', 'CUSTOMS_OFFICER', 'CARGO_INSPECTOR']}>
            <CargoPage />
          </ProtectedRoute>
        } 
      />
      
      <Route 
        path="/vehicle" 
        element={
          <ProtectedRoute requiredRoles={['ADMIN', 'CUSTOMS_OFFICER', 'VEHICLE_INSPECTOR']}>
            <VehiclePage />
          </ProtectedRoute>
        } 
      />
      
      <Route 
        path="/admin" 
        element={
          <ProtectedRoute requiredRoles={['ADMIN']}>
            <AdminPage />
          </ProtectedRoute>
        } 
      />
      
      {/* Default Route */}
      <Route 
        path="/" 
        element={
          <Navigate to={isAuthenticated ? "/dashboard" : "/login"} replace />
        } 
      />
      
      {/* Catch all route */}
      <Route 
        path="*" 
        element={
          <Navigate to={isAuthenticated ? "/dashboard" : "/login"} replace />
        } 
      />
    </Routes>
  );
};

export default AppRoutes;
