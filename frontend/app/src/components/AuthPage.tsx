import { useState } from 'react'
import LoginPanel from '../pages/LoginPage.tsx'
import RegistrationPanel from '../pages/RegisterPage.tsx'
import './../assets/LoginPanel.css'

type AuthPageProps = {
  onLogin: (creds: { email: string; password: string }) => Promise<void>
  onRegister: (creds: { firstName: string; lastName: string; email: string; password: string }) => Promise<void>
}

export default function AuthPage({ onLogin, onRegister }: AuthPageProps) {
  const [isLoginMode, setIsLoginMode] = useState(true)

  return (
    <div className="login-container">
      <div className="login-wrapper">
        {isLoginMode
          ? <LoginPanel onLogin={onLogin} />
          : <RegistrationPanel onRegister={onRegister} />
        }
        <div className="auth-switch">
          {isLoginMode ? 'Nie masz konta?' : 'Masz już konto?'}{' '}
          <button className="auth-switch-btn" onClick={() => setIsLoginMode(!isLoginMode)}>
            {isLoginMode ? 'Zarejestruj się' : 'Zaloguj się'}
          </button>
        </div>
      </div>
    </div>
  )
}
