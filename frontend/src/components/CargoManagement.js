import React, { useState, useEffect, useCallback } from 'react';
import cargoService from '../services/cargoService';
import { useAuth } from '../context/AuthContext';
import './CargoManagement.css';

const CargoManagement = () => {
  const { hasAuthority } = useAuth();
  const [cargoData, setCargoData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [statusFilter, setStatusFilter] = useState('');
  const [showCreateModal, setShowCreateModal] = useState(false);

  const [newCargo, setNewCargo] = useState({
    cargoId: '',
    description: '',
    origin: '',
    destination: '',
    weight: 0,
    volume: 0,
    declaredValue: 0,
    hsCode: '',
  });

  const loadCargo = useCallback(async () => {
    setLoading(true);
    setError('');
    
    try {
      const data = await cargoService.getAllCargo(
        currentPage,
        10,
        'createdAt',
        'desc',
        statusFilter || undefined
      );
      setCargoData(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load cargo data');
    } finally {
      setLoading(false);
    }
  }, [currentPage, statusFilter]);

  useEffect(() => {
    loadCargo();
  }, [loadCargo]);

  const handleCreateCargo = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await cargoService.createCargo(newCargo);
      setShowCreateModal(false);
      setNewCargo({
        cargoId: '',
        description: '',
        origin: '',
        destination: '',
        weight: 0,
        volume: 0,
        declaredValue: 0,
        hsCode: '',
      });
      loadCargo();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to create cargo entry');
    } finally {
      setLoading(false);
    }
  };

  const handleStatusUpdate = async (cargoId, newStatus) => {
    try {
      await cargoService.updateStatus(cargoId, newStatus);
      loadCargo();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to update status');
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDING_INSPECTION': return '#f59e0b';
      case 'UNDER_INSPECTION': return '#3b82f6';
      case 'INSPECTION_COMPLETED': return '#8b5cf6';
      case 'CLEARED': return '#10b981';
      case 'HELD': return '#ef4444';
      case 'REJECTED': return '#dc2626';
      case 'DUTY_PENDING': return '#f97316';
      case 'DUTY_PAID': return '#059669';
      default: return '#6b7280';
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString();
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  };

  const canCreate = hasAuthority('CREATE_CARGO');
  const canUpdate = hasAuthority('UPDATE_CARGO');

  return (
    <div className="cargo-management">
      <div className="page-header">
        <h1>Cargo Management</h1>
        <div className="header-actions">
          {canCreate && (
            <button 
              className="btn btn-primary"
              onClick={() => setShowCreateModal(true)}
            >
              Add New Cargo Entry
            </button>
          )}
        </div>
      </div>

      {error && (
        <div className="alert alert-danger">
          {error}
        </div>
      )}

      <div className="filters">
        <div className="filter-group">
          <label htmlFor="statusFilter">Filter by Status:</label>
          <select
            id="statusFilter"
            value={statusFilter}
            onChange={(e) => {
              setStatusFilter(e.target.value);
              setCurrentPage(0);
            }}
            className="form-control"
          >
            <option value="">All Statuses</option>
            <option value="PENDING_INSPECTION">Pending Inspection</option>
            <option value="UNDER_INSPECTION">Under Inspection</option>
            <option value="INSPECTION_COMPLETED">Inspection Completed</option>
            <option value="CLEARED">Cleared</option>
            <option value="HELD">Held</option>
            <option value="REJECTED">Rejected</option>
            <option value="DUTY_PENDING">Duty Pending</option>
            <option value="DUTY_PAID">Duty Paid</option>
          </select>
        </div>
      </div>

      {loading ? (
        <div className="loading-spinner">Loading cargo data...</div>
      ) : (
        <>
          <div className="cargo-grid">
            {cargoData?.content?.length > 0 ? (
              cargoData.content.map((cargo) => (
                <div key={cargo.id} className="cargo-card">
                  <div className="cargo-header">
                    <h3>{cargo.cargoId}</h3>
                    <span 
                      className="status-badge"
                      style={{ backgroundColor: getStatusColor(cargo.status) }}
                    >
                      {cargo.status?.replace('_', ' ')}
                    </span>
                  </div>
                  
                  <div className="cargo-details">
                    <p><strong>Description:</strong> {cargo.description}</p>
                    <p><strong>Origin:</strong> {cargo.origin}</p>
                    <p><strong>Destination:</strong> {cargo.destination}</p>
                    <p><strong>Weight:</strong> {cargo.weight} kg</p>
                    <p><strong>Volume:</strong> {cargo.volume} m³</p>
                    <p><strong>Declared Value:</strong> {formatCurrency(cargo.declaredValue)}</p>
                    <p><strong>HS Code:</strong> {cargo.hsCode}</p>
                    <p><strong>Created:</strong> {formatDate(cargo.createdAt)}</p>
                    {cargo.lastUpdated && (
                      <p><strong>Last Updated:</strong> {formatDate(cargo.lastUpdated)}</p>
                    )}
                  </div>

                  {canUpdate && (
                    <div className="cargo-actions">
                      <select
                        value={cargo.status}
                        onChange={(e) => handleStatusUpdate(cargo.id, e.target.value)}
                        className="form-control"
                      >
                        <option value="PENDING_INSPECTION">Pending Inspection</option>
                        <option value="UNDER_INSPECTION">Under Inspection</option>
                        <option value="INSPECTION_COMPLETED">Inspection Completed</option>
                        <option value="CLEARED">Cleared</option>
                        <option value="HELD">Held</option>
                        <option value="REJECTED">Rejected</option>
                        <option value="DUTY_PENDING">Duty Pending</option>
                        <option value="DUTY_PAID">Duty Paid</option>
                      </select>
                    </div>
                  )}
                </div>
              ))
            ) : (
              <div className="no-data">
                <p>No cargo entries found.</p>
                {canCreate && (
                  <button 
                    className="btn btn-primary"
                    onClick={() => setShowCreateModal(true)}
                  >
                    Create First Cargo Entry
                  </button>
                )}
              </div>
            )}
          </div>

          {cargoData?.totalPages > 1 && (
            <div className="pagination">
              <button
                onClick={() => setCurrentPage(prev => Math.max(0, prev - 1))}
                disabled={currentPage === 0}
                className="btn btn-secondary"
              >
                Previous
              </button>
              <span className="page-info">
                Page {currentPage + 1} of {cargoData.totalPages}
              </span>
              <button
                onClick={() => setCurrentPage(prev => Math.min(cargoData.totalPages - 1, prev + 1))}
                disabled={currentPage >= cargoData.totalPages - 1}
                className="btn btn-secondary"
              >
                Next
              </button>
            </div>
          )}
        </>
      )}

      {/* Create Cargo Modal */}
      {showCreateModal && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h2>Add New Cargo Entry</h2>
              <button
                className="close-button"
                onClick={() => setShowCreateModal(false)}
              >
                ×
              </button>
            </div>
            
            <form onSubmit={handleCreateCargo} className="modal-body">
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="cargoId">Cargo ID *</label>
                  <input
                    type="text"
                    id="cargoId"
                    className="form-control"
                    value={newCargo.cargoId}
                    onChange={(e) => setNewCargo({...newCargo, cargoId: e.target.value})}
                    required
                  />
                </div>
                
                <div className="form-group">
                  <label htmlFor="hsCode">HS Code *</label>
                  <input
                    type="text"
                    id="hsCode"
                    className="form-control"
                    value={newCargo.hsCode}
                    onChange={(e) => setNewCargo({...newCargo, hsCode: e.target.value})}
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="description">Description *</label>
                <textarea
                  id="description"
                  className="form-control"
                  value={newCargo.description}
                  onChange={(e) => setNewCargo({...newCargo, description: e.target.value})}
                  rows={3}
                  required
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="origin">Origin *</label>
                  <input
                    type="text"
                    id="origin"
                    className="form-control"
                    value={newCargo.origin}
                    onChange={(e) => setNewCargo({...newCargo, origin: e.target.value})}
                    required
                  />
                </div>
                
                <div className="form-group">
                  <label htmlFor="destination">Destination *</label>
                  <input
                    type="text"
                    id="destination"
                    className="form-control"
                    value={newCargo.destination}
                    onChange={(e) => setNewCargo({...newCargo, destination: e.target.value})}
                    required
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="weight">Weight (kg) *</label>
                  <input
                    type="number"
                    id="weight"
                    className="form-control"
                    value={newCargo.weight}
                    onChange={(e) => setNewCargo({...newCargo, weight: parseFloat(e.target.value) || 0})}
                    min="0"
                    step="0.01"
                    required
                  />
                </div>
                
                <div className="form-group">
                  <label htmlFor="volume">Volume (m³) *</label>
                  <input
                    type="number"
                    id="volume"
                    className="form-control"
                    value={newCargo.volume}
                    onChange={(e) => setNewCargo({...newCargo, volume: parseFloat(e.target.value) || 0})}
                    min="0"
                    step="0.01"
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="declaredValue">Declared Value (USD) *</label>
                <input
                  type="number"
                  id="declaredValue"
                  className="form-control"
                  value={newCargo.declaredValue}
                  onChange={(e) => setNewCargo({...newCargo, declaredValue: parseFloat(e.target.value) || 0})}
                  min="0"
                  step="0.01"
                  required
                />
              </div>

              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setShowCreateModal(false)}
                  disabled={loading}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={loading}
                >
                  {loading ? 'Creating...' : 'Create Cargo Entry'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default CargoManagement;
