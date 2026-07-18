const API_BASE_URL = import.meta.env.VITE_API_BASE_URL
  ?? (import.meta.env.PROD ? '/api/v1' : 'http://localhost:8080/api/v1')
const TOKEN_KEY = 'coinmaster_token'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function saveSession(session) {
  localStorage.setItem(TOKEN_KEY, session.token)
  localStorage.setItem('coinmaster_username', session.username)
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem('coinmaster_username')
}

export function getUsername() {
  return localStorage.getItem('coinmaster_username') ?? 'User'
}

async function request(path, options = {}) {
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  }
  const token = getToken()
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
  })

  const text = await response.text()
  let body = null
  if (text) {
    try {
      body = JSON.parse(text)
    } catch {
      body = { message: response.ok ? 'Invalid server response' : 'Service temporarily unavailable' }
    }
  }
  if (!response.ok) {
    throw new Error(body?.message ?? 'Request failed')
  }
  return body
}

export const api = {
  register(payload) {
    return request('/auth/register', {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
  login(payload) {
    return request('/auth/login', {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
  marketPrices() {
    return request('/market/prices')
  },
  portfolio() {
    return request('/portfolio')
  },
  trades(limit = 20) {
    return request(`/trades?limit=${limit}`)
  },
  executeTrade(side, payload) {
    return request(`/trades/${side.toLowerCase()}`, {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
  askAi(question, history = []) {
    return request('/ai/insights', {
      method: 'POST',
      body: JSON.stringify({ question, history }),
    })
  },
  aiStatus() {
    return request('/ai/status')
  },
}
