import React, { useState, FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { Role } from '../models/Role.ts'
import '../assets/AuthForms.css'

interface RegisterPageProps {
  onRegister: (creds: {
    firstName: string
    lastName: string
    email: string
    password: string
    role: Role
  }) => Promise<void>
}



const RegisterPage: React.FC<RegisterPageProps> = ({ onRegister }) => {
  const [firstName, setFirstName] = useState('')
  const [lastName, setLastName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [role, setRole] = useState<Role>(Role.USER)
  const [error, setError] = useState<string | null>(null)
  const navigate = useNavigate()

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError(null)
    try {
      await onRegister({ firstName, lastName, email, password, role })
      navigate('/login')
    } catch (err: any) {
      setError(err.message || 'Coś poszło nie tak')
    }
  }

  return (
    <div className="form-container">
      <form onSubmit={handleSubmit} className="register-form">
        <h2>Rejestracja</h2>
        {error && <div className="error">{error}</div>}
        <div className="field">
          <label>Imię</label>
          <input
            type="text"
            value={firstName}
            onChange={e => setFirstName(e.target.value)}
            required
          />
        </div>
        <div className="field">
          <label>Nazwisko</label>
          <input
            type="text"
            value={lastName}
            onChange={e => setLastName(e.target.value)}
            required
          />
        </div>
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
        <div className="field">
          <label>Rola</label>
          <select value={role} onChange={e => setRole(e.target.value as Role)}>
            <option value={Role.USER}>USER</option>
            <option value={Role.ADMIN}>ADMIN</option>
          </select>
        </div>
        <button type="submit">Zarejestruj</button>
      </form>
    </div>
  )
}

export default RegisterPage
