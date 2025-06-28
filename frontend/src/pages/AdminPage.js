import React from 'react';
import Layout from '../components/layout/Layout';

const AdminPage = () => {
  const users = [
    {
      id: 1,
      username: 'admin.tra',
      email: 'admin@tra.gov',
      role: 'ADMIN',
      status: 'Active',
      lastLogin: '2024-01-16 09:30'
    },
    {
      id: 2,
      username: 'john.smith',
      email: 'john.smith@tra.gov',
      role: 'CUSTOMS_OFFICER',
      status: 'Active',
      lastLogin: '2024-01-16 08:45'
    },
    {
      id: 3,
      username: 'jane.doe',
      email: 'jane.doe@tra.gov',
      role: 'CARGO_INSPECTOR',
      status: 'Active',
      lastLogin: '2024-01-15 16:20'
    },
    {
      id: 4,
      username: 'mike.wilson',
      email: 'mike.wilson@tra.gov',
      role: 'VEHICLE_INSPECTOR',
      status: 'Inactive',
      lastLogin: '2024-01-12 14:15'
    }
  ];

  const getRoleBadge = (role) => {
    switch (role) {
      case 'ADMIN':
        return 'status-danger';
      case 'CUSTOMS_OFFICER':
        return 'status-info';
      case 'CARGO_INSPECTOR':
        return 'status-warning';
      case 'VEHICLE_INSPECTOR':
        return 'status-success';
      default:
        return 'status-info';
    }
  };

  const getStatusBadge = (status) => {
    return status === 'Active' ? 'status-success' : 'status-danger';
  };

  return (
    <Layout>
      <div className="page-header">
        <h1>User Management</h1>
        <p>Manage system users, roles, and permissions</p>
      </div>

      <div className="action-buttons">
        <button className="btn btn-primary">
          Add New User
        </button>
        <button className="btn btn-secondary">
          Role Management
        </button>
        <button className="btn btn-secondary">
          System Settings
        </button>
      </div>

      <div className="data-table-container">
        <div className="data-table-header">
          <h3>System Users</h3>
          <span>{users.length} users</span>
        </div>
        <div className="data-table-content">
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Email</th>
                <th>Role</th>
                <th>Status</th>
                <th>Last Login</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user) => (
                <tr key={user.id}>
                  <td>{user.id}</td>
                  <td>{user.username}</td>
                  <td>{user.email}</td>
                  <td>
                    <span className={`status-badge ${getRoleBadge(user.role)}`}>
                      {user.role.replace('_', ' ')}
                    </span>
                  </td>
                  <td>
                    <span className={`status-badge ${getStatusBadge(user.status)}`}>
                      {user.status}
                    </span>
                  </td>
                  <td>{user.lastLogin}</td>
                  <td>
                    <button className="btn btn-primary" style={{ marginRight: '0.5rem', minWidth: 'auto', padding: '0.25rem 0.5rem' }}>
                      Edit
                    </button>
                    <button className="btn btn-danger" style={{ minWidth: 'auto', padding: '0.25rem 0.5rem' }}>
                      Disable
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <div className="dashboard-grid" style={{ marginTop: '2rem' }}>
        <div className="dashboard-card">
          <div className="dashboard-card-icon">👥</div>
          <div className="dashboard-card-title">Total Users</div>
          <div className="dashboard-card-value">{users.length}</div>
          <div className="dashboard-card-description">System users</div>
        </div>
        <div className="dashboard-card">
          <div className="dashboard-card-icon">✅</div>
          <div className="dashboard-card-title">Active Users</div>
          <div className="dashboard-card-value">{users.filter(u => u.status === 'Active').length}</div>
          <div className="dashboard-card-description">Currently active</div>
        </div>
        <div className="dashboard-card">
          <div className="dashboard-card-icon">🔒</div>
          <div className="dashboard-card-title">Admin Users</div>
          <div className="dashboard-card-value">{users.filter(u => u.role === 'ADMIN').length}</div>
          <div className="dashboard-card-description">System administrators</div>
        </div>
        <div className="dashboard-card">
          <div className="dashboard-card-icon">🔄</div>
          <div className="dashboard-card-title">Recent Logins</div>
          <div className="dashboard-card-value">{users.filter(u => u.lastLogin.includes('2024-01-16')).length}</div>
          <div className="dashboard-card-description">Today</div>
        </div>
      </div>
    </Layout>
  );
};

export default AdminPage;
