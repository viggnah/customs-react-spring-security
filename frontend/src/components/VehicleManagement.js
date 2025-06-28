import React from 'react';
import { useAuth } from '../context/AuthContext';
import './VehicleManagement.css';

const VehicleManagement = () => {
  const { hasAuthority } = useAuth();

  return (
    <div className="vehicle-management">
      <div className="page-header">
        <h1>Vehicle Import Management</h1>
        <div className="page-actions">
          {hasAuthority('CREATE_VEHICLE') && (
            <button className="create-button">
              Register New Vehicle
            </button>
          )}
        </div>
      </div>

      <div className="empty-state">
        <div className="empty-icon">🚗</div>
        <h3>Vehicle Management Coming Soon</h3>
        <p>
          This module will handle vehicle import registrations, inspections, and approvals.
          Similar functionality to cargo management but tailored for vehicle imports.
        </p>
        <div className="features-list">
          <div className="feature-item">
            <span className="feature-icon">📋</span>
            <span>Vehicle Registration</span>
          </div>
          <div className="feature-item">
            <span className="feature-icon">🔍</span>
            <span>Import Inspections</span>
          </div>
          <div className="feature-item">
            <span className="feature-icon">💰</span>
            <span>Duty Calculations</span>
          </div>
          <div className="feature-item">
            <span className="feature-icon">✅</span>
            <span>Approval Workflow</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default VehicleManagement;
