import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import { WSO2AuthProvider } from './context/WSO2AuthContext';
import AppRoutes from './AppRoutes';
import './styles/App.css';

function App() {
  return (
    <WSO2AuthProvider>
      <Router>
        <div className="App">
          <AppRoutes />
        </div>
      </Router>
    </WSO2AuthProvider>
  );
}

export default App;
