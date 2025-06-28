import React from 'react';
import { useAuth } from '../context/WSO2AuthContext';
import './Dashboard.css';

const Dashboard = () => {
  const { user } = useAuth();

  const stats = [
    { label: 'Total Cargo Entries', value: '1', color: '#3b82f6' },
    { label: 'Pending Inspections', value: '1', color: '#f59e0b' },
    { label: 'Cleared Cargo', value: '0', color: '#10b981' },
    { label: 'Vehicle Imports', value: '0', color: '#8b5cf6' },
  ];

  const recentActivities = [
    { id: 1, description: 'System initialized', time: 'Just now', type: 'info' },
    { id: 2, description: 'User logged in successfully', time: 'Just now', type: 'success' },
  ];

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h1>Welcome back, {user?.username}!</h1>
        <p>Here's an overview of your customs management activities</p>
      </div>

      <div className="stats-grid">
        {stats.map((stat, index) => (
          <div key={index} className="stat-card">
            <div className="stat-icon" style={{ backgroundColor: stat.color }}>
              📊
            </div>
            <div className="stat-content">
              <h3>{stat.value}</h3>
              <p>{stat.label}</p>
            </div>
          </div>
        ))}
      </div>

      <div className="dashboard-content">
        <div className="recent-activities">
          <h2>Recent Activities</h2>
          <div className="activities-list">
            {recentActivities.map(activity => (
              <div key={activity.id} className={`activity-item ${activity.type}`}>
                <div className="activity-content">
                  <p>{activity.description}</p>
                  <span className="activity-time">{activity.time}</span>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="quick-actions">
          <h2>Quick Actions</h2>
          <div className="actions-grid">
            {user?.authorities?.includes('CREATE_CARGO') && (
              <button className="action-button cargo">
                <span className="action-icon">📦</span>
                <span>Add New Cargo</span>
              </button>
            )}
            {user?.authorities?.includes('CREATE_VEHICLE') && (
              <button className="action-button vehicle">
                <span className="action-icon">🚗</span>
                <span>Register Vehicle</span>
              </button>
            )}
            {user?.authorities?.includes('VIEW_REPORTS') && (
              <button className="action-button report">
                <span className="action-icon">📄</span>
                <span>Generate Report</span>
              </button>
            )}
            {user?.authorities?.includes('INSPECT_CARGO') && (
              <button className="action-button inspect">
                <span className="action-icon">🔍</span>
                <span>Start Inspection</span>
              </button>
            )}
          </div>
        </div>
      </div>

      <div className="user-permissions">
        <h2>Your Permissions</h2>
        <div className="permissions-grid">
          <div className="permission-section">
            <h3>Roles</h3>
            <div className="permission-tags">
              {user?.roles?.map(role => (
                <span key={role} className="permission-tag role">
                  {role}
                </span>
              ))}
            </div>
          </div>
          <div className="permission-section">
            <h3>Authorities</h3>
            <div className="permission-tags">
              {user?.authorities?.slice(0, 8).map(authority => (
                <span key={authority} className="permission-tag authority">
                  {authority}
                </span>
              ))}
              {user && user.authorities && user.authorities.length > 8 && (
                <span className="permission-tag more">
                  +{user.authorities.length - 8} more
                </span>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
