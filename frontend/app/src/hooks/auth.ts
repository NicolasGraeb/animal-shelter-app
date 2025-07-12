export const TOKEN_KEY = 'accessToken'
export const REFRESH_TOKEN_KEY = 'refreshToken'

export function setToken(token: string) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(REFRESH_TOKEN_KEY)
}

export function setRefreshToken(token: string) {
  localStorage.setItem(REFRESH_TOKEN_KEY, token)
}

export async function refreshToken() {
  const refresh = localStorage.getItem(REFRESH_TOKEN_KEY)
  if (!refresh) return false
  try {
    const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/auth/refresh`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ refreshToken: refresh }),
    })
    if (!response.ok) return false
    const data = await response.json()
    setToken(data.accessToken)
    setRefreshToken(data.refreshToken)
    return true
  } catch (error) {
    console.error('Token refresh failed:', error)
    return false
  }
}
