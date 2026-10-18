import React from 'react';
import { useNavigate } from 'react-router-dom';
import { session } from '../../api';
import './Header.css';

const Header = () => {
  const navigate = useNavigate();

  const logout = () => {
    session.clear();
    navigate('/login', { replace: true });
  };

  return (
    <header className="nav-header">
      <div className="nav-container">
        <div className="nav-logo">
          <span className="logo-icon">🌊</span>
          <span className="logo-text">SaschaEvents</span>
        </div>

        <div className="nav-auth">
          <span className="nav-user">
            {session.username}
            {session.isAdmin && <span className="nav-badge">admin</span>}
          </span>
          <button type="button" className="logout-link" onClick={logout}>
            Log out →
          </button>
        </div>
      </div>
    </header>
  );
};

export default Header;
