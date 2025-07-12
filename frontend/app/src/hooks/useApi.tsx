import { getToken, refreshToken } from './auth'

const BASE = import.meta.env.VITE_API_BASE_URL

export function useApi() {
  async function request(path: string, opts: RequestInit) {
    const url = `${BASE}/api${path}`

    const isForm = opts.body instanceof FormData
    console.log(
      `[API request] ${opts.method} ${url}`,
      'headers:', opts.headers,
      isForm
        ? 'formData:' + JSON.stringify(Array.from((opts.body as FormData).entries()))
        : 'body:' + opts.body
    )

    try {
      const res = await fetch(url, opts)

      if (res.status === 401) {
        const refreshed = await refreshToken()
        if (refreshed) {
          const newHeaders = { ...opts.headers } as Record<string, string>
          newHeaders['Authorization'] = `Bearer ${getToken()}`
          const retryRes = await fetch(url, { ...opts, headers: newHeaders })
          if (!retryRes.ok) throw new Error(await retryRes.text() || retryRes.statusText)
          return retryRes.headers.get('content-type')?.includes('application/json')
            ? retryRes.json()
            : null
        }
      }

      if (!res.ok) {
        const text = await res.text()
        throw new Error(text || res.statusText)
      }

      return res.headers.get('content-type')?.includes('application/json')
        ? res.json()
        : null
    } catch (err) {
      console.error('API request error:', err)
      throw err
    }
  }

  function authHeaders(isJson: boolean, additional: HeadersInit = {}) {
    const headers: Record<string,string> = { ...(additional as any) }
    const token = getToken()
    if (token) headers['Authorization'] = `Bearer ${token}`
    if (isJson) headers['Content-Type'] = 'application/json'
    return headers
  }

  return {
    get: (path: string) =>
      request(path, { method: 'GET', headers: authHeaders(true) }),

    post: (path: string, data?: any) => {
      const isForm = data instanceof FormData
      return request(path, {
        method: 'POST',
        headers: authHeaders(!isForm),
        body: isForm ? data : JSON.stringify(data),
      })
    },

    put: (path: string, data: any) =>
      request(path, {
        method: 'PUT',
        headers: authHeaders(true),
        body: JSON.stringify(data),
      }),

    patch: (path: string, data: any) =>
      request(path, {
        method: 'PATCH',
        headers: authHeaders(true),
        body: JSON.stringify(data),
      }),

    del: (path: string) =>
      request(path, {
        method: 'DELETE',
        headers: authHeaders(true),
      }),
  }
}
