import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const Sidebar = () => {
  const location = useLocation();
  const { user } = useAuth();

  const menuItems = [
    {
      path: '/dashboard',
      icon: '📊',
      label: 'Dashboard',
      roles: ['ADMIN', 'CUSTOMS_OFFICER', 'CARGO_INSPECTOR', 'VEHICLE_INSPECTOR', 'DUTY_OFFICER']
    },
    {
      path: '/cargo',
      icon: '📦',
      label: 'Cargo Clearance',
      roles: ['ADMIN', 'CUSTOMS_OFFICER', 'CARGO_INSPECTOR']
    },
    {
      path: '/vehicle',
      icon: '🚗',
      label: 'Vehicle Import',
      roles: ['ADMIN', 'CUSTOMS_OFFICER', 'VEHICLE_INSPECTOR']
    },
    {
      path: '/duties',
      icon: '💰',
      label: 'Duty Management',
      roles: ['ADMIN', 'CUSTOMS_OFFICER', 'DUTY_OFFICER']
    },
    {
      path: '/reports',
      icon: '📈',
      label: 'Reports',
      roles: ['ADMIN', 'CUSTOMS_OFFICER']
    },
    {
      path: '/admin',
      icon: '⚙️',
      label: 'Administration',
      roles: ['ADMIN']
    }
  ];

  const hasAccess = (itemRoles) => {
    if (!user || !user.roles) return false;
    return itemRoles.some(role => user.roles.includes(role));
  };

  const isActive = (path) => {
    return location.pathname === path;
  };

  return (
    <div className="sidebar">
      <div style={{
        padding: 'var(--spacing-lg)',
        borderBottom: '1px solid var(--customs-gray)',
        textAlign: 'center'
      }}>
        <h3 style={{
          color: 'var(--customs-yellow)',
          margin: 0,
          fontSize: 'var(--font-size-large)'
        }}>
          CUSTOMS
        </h3>
        <p style={{
          color: 'var(--customs-white)',
          margin: '0.25rem 0 0 0',
          fontSize: 'var(--font-size-small)'
        }}>
          Customs System
        </p>
      </div>

      <nav style={{ padding: 'var(--spacing-md) 0' }}>
        {menuItems.map((item) => {
          if (!hasAccess(item.roles)) return null;

          return (
            <Link
              key={item.path}
              to={item.path}
              style={{
                display: 'flex',
                alignItems: 'center',
                padding: 'var(--spacing-md) var(--spacing-lg)',
                color: isActive(item.path) ? 'var(--customs-yellow)' : 'var(--customs-white)',
                backgroundColor: isActive(item.path) ? 'var(--customs-gray)' : 'transparent',
                textDecoration: 'none',
                transition: 'all 0.3s ease',
                borderLeft: isActive(item.path) ? '4px solid var(--customs-yellow)' : '4px solid transparent'
              }}
              onMouseEnter={(e) => {
                if (!isActive(item.path)) {
                  e.target.style.backgroundColor = 'var(--customs-dark-gray)';
                }
              }}
              onMouseLeave={(e) => {
                if (!isActive(item.path)) {
                  e.target.style.backgroundColor = 'transparent';
                }
              }}
            >
              <span style={{
                marginRight: 'var(--spacing-md)',
                fontSize: '1.2em'
              }}>
                {item.icon}
              </span>
              <span>{item.label}</span>
            </Link>
          );
        })}
      </nav>

      <div style={{
        position: 'absolute',
        bottom: 'var(--spacing-lg)',
        left: 'var(--spacing-lg)',
        right: 'var(--spacing-lg)',
        padding: 'var(--spacing-md)',
        backgroundColor: 'var(--customs-gray)',
        borderRadius: 'var(--border-radius-md)',
        fontSize: 'var(--font-size-small)',
        color: 'var(--customs-white)',
        boxSizing: 'border-box'
      }}>
        <div><strong>User:</strong> {user?.username}</div>
        <div><strong>Role:</strong> {user?.roles?.[0]?.replace('_', ' ')}</div>
      </div>
    </div>
  );
};

export default Sidebar;
