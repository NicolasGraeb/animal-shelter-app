import { Link } from 'react-router-dom'
import React from 'react'
import './Header.css'

interface HeaderProps {
  isAuthenticated: boolean
  onLogout: () => void
}

export default function Header({ isAuthenticated, onLogout }: HeaderProps) {
  return (
    <header className="header">
      <Link to="/"><img src="/bone.png" alt="Logo" className="logo" /></Link>
      <nav className="nav">
        <Link to="/">Home</Link>
        <Link to="/adopt">Adopt</Link>
        {isAuthenticated && <Link to="/profile">Profile</Link>}
      </nav>
      <div className="auth-buttons">
        {isAuthenticated ? (
          <button className="logout-btn" onClick={onLogout}>Wyloguj</button>
        ) : (
          <>
            <Link to="/login"><button className="login-btn">Zaloguj</button></Link>
            <Link to="/register"><button className="register-btn">Zarejestruj</button></Link>
          </>
        )}
      </div>
    </header>
  )
}
