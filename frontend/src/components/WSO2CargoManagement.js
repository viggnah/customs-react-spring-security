import React, { useState, useEffect, useCallback } from 'react';
import { useCargoService } from '../services/wso2CargoService';
import { useAuth } from '../context/WSO2AuthContext';

const WSO2CargoManagement = () => {
  const { hasAuthority, isAuthenticated, user } = useAuth();
  const cargoService = useCargoService();
  
  const [cargoData, setCargoData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
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
    if (!isAuthenticated) return;
    
    setLoading(true);
    setError('');
    
    try {
      let data;
      if (statusFilter) {
        data = await cargoService.searchCargo({ status: statusFilter });
      } else {
        data = await cargoService.getAllCargo();
      }
      setCargoData(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load cargo data');
    } finally {
      setLoading(false);
    }
  }, [statusFilter, isAuthenticated]);

  useEffect(() => {
    loadCargo();
  }, [loadCargo]);

  const handleCreateCargo = async (e) => {
    e.preventDefault();
    if (!hasAuthority('CREATE_CARGO')) {
      setError('You do not have permission to create cargo entries');
      return;
    }

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
      await loadCargo(); // Reload data
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to create cargo');
    } finally {
      setLoading(false);
    }
  };

  const handleStatusChange = async (cargoId, newStatus) => {
    if (!hasAuthority('CREATE_CARGO')) {
      setError('You do not have permission to update cargo status');
      return;
    }

    try {
      await cargoService.updateCargoStatus(cargoId, newStatus);
      await loadCargo(); // Reload data
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to update cargo status');
    }
  };

  const handleDeleteCargo = async (cargoId) => {
    if (!hasAuthority('DELETE')) {
      setError('You do not have permission to delete cargo entries');
      return;
    }

    if (!window.confirm('Are you sure you want to delete this cargo entry?')) {
      return;
    }

    try {
      await cargoService.deleteCargo(cargoId);
      await loadCargo(); // Reload data
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to delete cargo');
    }
  };

  if (!isAuthenticated) {
    return (
      <div style={{ padding: 'var(--spacing-lg)', textAlign: 'center' }}>
        <p>Please log in to access cargo management.</p>
      </div>
    );
  }

  return (
    <div style={{ padding: 'var(--spacing-lg)' }}>
      <div style={{ 
        display: 'flex', 
        justifyContent: 'space-between', 
        alignItems: 'center',
        marginBottom: 'var(--spacing-lg)'
      }}>
        <h2 style={{ margin: 0 }}>WSO2 Cargo Management</h2>
        {hasAuthority('CREATE_CARGO') && (
          <button
            onClick={() => setShowCreateModal(true)}
            style={{
              padding: 'var(--spacing-sm) var(--spacing-md)',
              backgroundColor: 'var(--customs-blue)',
              color: 'var(--customs-white)',
              border: 'none',
              borderRadius: 'var(--border-radius-sm)',
              cursor: 'pointer'
            }}
          >
            Create New Cargo
          </button>
        )}
      </div>

      {/* Status Filter */}
      <div style={{ marginBottom: 'var(--spacing-lg)' }}>
        <label htmlFor="status-filter">Filter by Status:</label>
        <select
          id="status-filter"
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          style={{
            marginLeft: 'var(--spacing-sm)',
            padding: 'var(--spacing-xs)',
            border: '1px solid var(--customs-gray)',
            borderRadius: 'var(--border-radius-sm)'
          }}
        >
          <option value="">All</option>
          <option value="PENDING">Pending</option>
          <option value="IN_TRANSIT">In Transit</option>
          <option value="ARRIVED">Arrived</option>
          <option value="CLEARED">Cleared</option>
          <option value="RELEASED">Released</option>
        </select>
      </div>

      {/* Error Display */}
      {error && (
        <div style={{
          backgroundColor: '#ffebee',
          color: '#c62828',
          padding: 'var(--spacing-md)',
          borderRadius: 'var(--border-radius-sm)',
          marginBottom: 'var(--spacing-lg)',
          border: '1px solid #ffcdd2'
        }}>
          {error}
        </div>
      )}

      {/* Loading State */}
      {loading && (
        <div style={{ textAlign: 'center', padding: 'var(--spacing-lg)' }}>
          <p>Loading cargo data...</p>
        </div>
      )}

      {/* Cargo List */}
      {!loading && cargoData.length > 0 && (
        <div style={{
          backgroundColor: 'var(--customs-white)',
          borderRadius: 'var(--border-radius-md)',
          boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
          overflow: 'hidden'
        }}>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead style={{ backgroundColor: 'var(--customs-light-gray)' }}>
              <tr>
                <th style={{ padding: 'var(--spacing-md)', textAlign: 'left' }}>Cargo ID</th>
                <th style={{ padding: 'var(--spacing-md)', textAlign: 'left' }}>Description</th>
                <th style={{ padding: 'var(--spacing-md)', textAlign: 'left' }}>Origin</th>
                <th style={{ padding: 'var(--spacing-md)', textAlign: 'left' }}>Destination</th>
                <th style={{ padding: 'var(--spacing-md)', textAlign: 'left' }}>Status</th>
                <th style={{ padding: 'var(--spacing-md)', textAlign: 'left' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {cargoData.map((cargo) => (
                <tr key={cargo.id} style={{ borderTop: '1px solid var(--customs-light-gray)' }}>
                  <td style={{ padding: 'var(--spacing-md)' }}>{cargo.cargoId}</td>
                  <td style={{ padding: 'var(--spacing-md)' }}>{cargo.description}</td>
                  <td style={{ padding: 'var(--spacing-md)' }}>{cargo.origin}</td>
                  <td style={{ padding: 'var(--spacing-md)' }}>{cargo.destination}</td>
                  <td style={{ padding: 'var(--spacing-md)' }}>
                    <span style={{
                      padding: 'var(--spacing-xs) var(--spacing-sm)',
                      borderRadius: 'var(--border-radius-sm)',
                      fontSize: 'var(--font-size-small)',
                      backgroundColor: cargo.status === 'CLEARED' ? '#e8f5e8' : '#fff3cd',
                      color: cargo.status === 'CLEARED' ? '#2e7d32' : '#856404'
                    }}>
                      {cargo.status}
                    </span>
                  </td>
                  <td style={{ padding: 'var(--spacing-md)' }}>
                    {hasAuthority('CREATE_CARGO') && (
                      <button
                        onClick={() => handleStatusChange(cargo.id, 'CLEARED')}
                        disabled={cargo.status === 'CLEARED'}
                        style={{
                          padding: 'var(--spacing-xs) var(--spacing-sm)',
                          backgroundColor: 'var(--customs-yellow)',
                          color: 'var(--customs-dark-blue)',
                          border: 'none',
                          borderRadius: 'var(--border-radius-sm)',
                          cursor: cargo.status === 'CLEARED' ? 'not-allowed' : 'pointer',
                          marginRight: 'var(--spacing-xs)',
                          opacity: cargo.status === 'CLEARED' ? 0.5 : 1
                        }}
                      >
                        Clear
                      </button>
                    )}
                    {hasAuthority('DELETE') && (
                      <button
                        onClick={() => handleDeleteCargo(cargo.id)}
                        style={{
                          padding: 'var(--spacing-xs) var(--spacing-sm)',
                          backgroundColor: '#f44336',
                          color: 'white',
                          border: 'none',
                          borderRadius: 'var(--border-radius-sm)',
                          cursor: 'pointer'
                        }}
                      >
                        Delete
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Empty State */}
      {!loading && cargoData.length === 0 && (
        <div style={{
          textAlign: 'center',
          padding: 'var(--spacing-xxl)',
          backgroundColor: 'var(--customs-white)',
          borderRadius: 'var(--border-radius-md)',
          boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
        }}>
          <p style={{ margin: 0, color: 'var(--customs-gray)' }}>
            No cargo entries found. {hasAuthority('CREATE_CARGO') && 'Create one to get started.'}
          </p>
        </div>
      )}

      {/* Create Modal */}
      {showCreateModal && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.5)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 1000
        }}>
          <div style={{
            backgroundColor: 'var(--customs-white)',
            padding: 'var(--spacing-lg)',
            borderRadius: 'var(--border-radius-md)',
            width: '90%',
            maxWidth: '500px',
            maxHeight: '90vh',
            overflow: 'auto'
          }}>
            <h3 style={{ marginTop: 0 }}>Create New Cargo</h3>
            <form onSubmit={handleCreateCargo}>
              <div style={{ marginBottom: 'var(--spacing-md)' }}>
                <label>Cargo ID:</label>
                <input
                  type="text"
                  value={newCargo.cargoId}
                  onChange={(e) => setNewCargo({ ...newCargo, cargoId: e.target.value })}
                  required
                  style={{
                    width: '100%',
                    padding: 'var(--spacing-sm)',
                    border: '1px solid var(--customs-gray)',
                    borderRadius: 'var(--border-radius-sm)',
                    marginTop: 'var(--spacing-xs)'
                  }}
                />
              </div>
              <div style={{ marginBottom: 'var(--spacing-md)' }}>
                <label>Description:</label>
                <textarea
                  value={newCargo.description}
                  onChange={(e) => setNewCargo({ ...newCargo, description: e.target.value })}
                  required
                  rows={3}
                  style={{
                    width: '100%',
                    padding: 'var(--spacing-sm)',
                    border: '1px solid var(--customs-gray)',
                    borderRadius: 'var(--border-radius-sm)',
                    marginTop: 'var(--spacing-xs)',
                    resize: 'vertical'
                  }}
                />
              </div>
              <div style={{ display: 'flex', gap: 'var(--spacing-md)', marginBottom: 'var(--spacing-md)' }}>
                <div style={{ flex: 1 }}>
                  <label>Origin:</label>
                  <input
                    type="text"
                    value={newCargo.origin}
                    onChange={(e) => setNewCargo({ ...newCargo, origin: e.target.value })}
                    required
                    style={{
                      width: '100%',
                      padding: 'var(--spacing-sm)',
                      border: '1px solid var(--customs-gray)',
                      borderRadius: 'var(--border-radius-sm)',
                      marginTop: 'var(--spacing-xs)'
                    }}
                  />
                </div>
                <div style={{ flex: 1 }}>
                  <label>Destination:</label>
                  <input
                    type="text"
                    value={newCargo.destination}
                    onChange={(e) => setNewCargo({ ...newCargo, destination: e.target.value })}
                    required
                    style={{
                      width: '100%',
                      padding: 'var(--spacing-sm)',
                      border: '1px solid var(--customs-gray)',
                      borderRadius: 'var(--border-radius-sm)',
                      marginTop: 'var(--spacing-xs)'
                    }}
                  />
                </div>
              </div>
              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 'var(--spacing-md)' }}>
                <button
                  type="button"
                  onClick={() => setShowCreateModal(false)}
                  style={{
                    padding: 'var(--spacing-sm) var(--spacing-md)',
                    backgroundColor: 'var(--customs-gray)',
                    color: 'var(--customs-white)',
                    border: 'none',
                    borderRadius: 'var(--border-radius-sm)',
                    cursor: 'pointer'
                  }}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={loading}
                  style={{
                    padding: 'var(--spacing-sm) var(--spacing-md)',
                    backgroundColor: 'var(--customs-blue)',
                    color: 'var(--customs-white)',
                    border: 'none',
                    borderRadius: 'var(--border-radius-sm)',
                    cursor: loading ? 'not-allowed' : 'pointer',
                    opacity: loading ? 0.6 : 1
                  }}
                >
                  {loading ? 'Creating...' : 'Create Cargo'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default WSO2CargoManagement;
