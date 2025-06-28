import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';

const LoginPage = () => {
  const { login } = useAuth();
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await login(formData);
    } catch (error) {
      setError(error.message || 'Login failed. Please try again.');
    }
    
    setLoading(false);
  };

  return (
    <div className="auth-layout">
      <div className="auth-card">
        <div className="auth-logo">
          <h1>
            <span className="logo-text">TRA</span> Customs
          </h1>
          <p>Tax Revenue Authority</p>
        </div>

        {error && (
          <div className="alert alert-danger">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="username" className="form-label">
              Username
            </label>
            <input
              type="text"
              id="username"
              name="username"
              className="form-control"
              value={formData.username}
              onChange={handleChange}
              placeholder="firstname.lastname"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password" className="form-label">
              Password
            </label>
            <input
              type="password"
              id="password"
              name="password"
              className="form-control"
              value={formData.password}
              onChange={handleChange}
              placeholder="Enter your password"
              required
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            disabled={loading}
            style={{ width: '100%', marginBottom: '1rem' }}
          >
            {loading ? 'Signing In...' : 'Sign In'}
          </button>
        </form>

        <div className="text-center">
          <a href="#forgot" className="text-primary">
            Forgot your password?
          </a>
        </div>

        <div className="mt-4">
          <h4>Demo Users:</h4>
          <ul style={{ fontSize: '0.875rem', color: '#666' }}>
            <li><strong>admin.tra</strong> / admin123 (Admin)</li>
            <li><strong>john.smith</strong> / customs123 (Customs Officer)</li>
            <li><strong>jane.doe</strong> / cargo123 (Cargo Inspector)</li>
            <li><strong>mike.wilson</strong> / vehicle123 (Vehicle Inspector)</li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
