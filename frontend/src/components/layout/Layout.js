import React from 'react';
import Header from './Header';
import Sidebar from './Sidebar';

const Layout = ({ children }) => {
  return (
    <div className="main-layout">
      <Sidebar />
      <div className="main-content">
        <Header />
        <div className="content-area">
          {children}
        </div>
      </div>
    </div>
  );
};

export default Layout;
