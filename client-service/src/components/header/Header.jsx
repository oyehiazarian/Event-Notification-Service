import React from "react";
import "./Header.css";

const Header = () => {
  return (
    <header className="nav-header">
      <div className="nav-container">
      <div className="nav-logo">
        <span className="logo-icon">🌊</span>
        <span className="logo-text">SanyaEvent</span>
      </div>

        <nav className="nav-menu">
          <a href="#">Product</a>
          <a href="#">Features</a>
          <a href="#">Marketplace</a>
          <a href="#">Company</a>
        </nav>
        <div className="nav-auth">
          <a href="#" className="logout-link">Log out →</a>
        </div>
      </div>
    </header>
  );
};

export default Header;
