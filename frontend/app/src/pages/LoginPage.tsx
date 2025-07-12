import React, { useState, FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import '../assets/AuthForms.css'

interface LoginPageProps {
  onLogin: (creds: { email: string; password: string }) => Promise<void>
}

const LoginPage: React.FC<LoginPageProps> = ({ onLogin }) => {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const navigate = useNavigate()

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError(null)
    try {
      await onLogin({ email, password })
      navigate('/')
    } catch (err: any) {
      setError(err.message || 'Coś poszło nie tak')
    }
  }

  return (
    <div className="form-container">
      <form onSubmit={handleSubmit} className="login-form">
        <h2>Logowanie</h2>
        {error && <div className="error">{error}</div>}
        <div className="field">
          <label>Email</label>
          <input
            type="email"
            value={email}
            onChange={e => setEmail(e.target.value)}
            required
          />
        </div>
        <div className="field">
          <label>Hasło</label>
          <input
            type="password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
          />
        </div>
        <button type="submit">Zaloguj</button>
      </form>
    </div>
  )
}

export default LoginPage
