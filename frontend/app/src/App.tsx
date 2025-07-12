import React, { useState, FC, useEffect } from 'react'
import { BrowserRouter, Routes, Route, Navigate, useNavigate } from 'react-router-dom'
import Header from './components/Header'
import Home from './pages/Home'
import Adopt from './pages/Adopt'
import AnimalDetails from './pages/AnimalDetails'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import ProfilePage from './pages/ProfilePage'

const App: FC = () => {
  const apiURL = import.meta.env.VITE_API_BASE_URL

  const [isAuthenticated, setIsAuthenticated] = useState(() =>
    Boolean(localStorage.getItem('accessToken'))
  )

  const handleLogin = async (creds: { email: string; password: string }) => {
  const res = await fetch(`${apiURL}/api/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: creds.email, password: creds.password }),
  })
  if (!res.ok) throw new Error('Login failed')

  const { accessToken, refreshToken } = await res.json()
  localStorage.setItem('accessToken', accessToken)
  localStorage.setItem('refreshToken', refreshToken)
  setIsAuthenticated(true)

  try {
    // decode the JWT payload
    const base64Payload = accessToken.split('.')[1]
    const payloadJson = atob(base64Payload.replace(/-/g, '+').replace(/_/g, '/'))
    const payload = JSON.parse(payloadJson)
    console.log('Decoded JWT payload:', payload)
  } catch (e) {
    console.warn('Failed to decode JWT:', e)
  }
}


  const handleRegister = async (creds: {
    firstName: string
    lastName: string
    email: string
    password: string
    role: string
  }) => {
    const res = await fetch(`${apiURL}/api/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(creds),
    })
    if (!res.ok) throw new Error('Registration failed')
    const { accessToken, refreshToken } = await res.json()
    localStorage.setItem('accessToken', accessToken)
    localStorage.setItem('refreshToken', refreshToken)
    setIsAuthenticated(true)
  }

  const handleLogout = () => {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    setIsAuthenticated(false)
  }

  return (
    <BrowserRouter>
      <Header
        isAuthenticated={isAuthenticated}
        onLogin={handleLogin}
        onRegister={handleRegister}
        onLogout={handleLogout}
      />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<LoginPage onLogin={handleLogin} />} />
        <Route
          path="/register"
          element={<RegisterPage onRegister={handleRegister} />}
        />
        <Route
          path="/profile"
          element={
            isAuthenticated ? <ProfilePage /> : <Navigate to="/login" replace />
          }
        />
        <Route
          path="/adopt"
          element={
            isAuthenticated ? <Adopt /> : <Navigate to="/login" replace />
          }
        />
        <Route path="*" element={<Navigate to="/" replace />} />
        <Route path="/animals/:id" element={<AnimalDetails />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
