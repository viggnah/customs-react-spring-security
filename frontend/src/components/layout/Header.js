import React from 'react';
import { useAuth } from '../../context/AuthContext';

const Header = () => {
  const { user, logout } = useAuth();

  return (
    <header style={{
      backgroundColor: 'var(--tra-yellow)',
      color: 'var(--tra-black)',
      padding: 'var(--spacing-md) var(--spacing-lg)',
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      borderBottom: '2px solid var(--tra-black)'
    }}>
      <div>
        <h2 style={{ margin: 0, fontSize: 'var(--font-size-large)' }}>
          TRA Customs Management System
        </h2>
      </div>
      
      <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--spacing-md)' }}>
        <span>Welcome, {user?.username || 'User'}</span>
        <button 
          onClick={logout}
          className="btn btn-secondary"
          style={{ minWidth: 'auto', padding: '0.5rem 1rem' }}
        >
          Logout
        </button>
      </div>
    </header>
  );
};

export default Header;
