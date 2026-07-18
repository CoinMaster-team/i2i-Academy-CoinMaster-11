import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api, saveSession } from '../api'
import '../App.css'

function LoginPage() {
  const navigate = useNavigate()
  const [formData, setFormData] = useState({
    username: '',
    password: '',
  })

  const [message, setMessage] = useState('')
  const [messageType, setMessageType] = useState('')

  function handleChange(event) {
    const { name, value } = event.target

    setFormData((currentData) => ({
      ...currentData,
      [name]: value,
    }))
  }

  const [isSubmitting, setIsSubmitting] = useState(false)

  async function handleSubmit(event) {
    event.preventDefault()

    if (!formData.username.trim() || !formData.password.trim()) {
      setMessage('Please enter your username and password.')
      setMessageType('error')
      return
    }

    try {
      setIsSubmitting(true)
      const session = await api.login({
        username: formData.username,
        password: formData.password,
      })
      saveSession(session)
      navigate('/dashboard')
    } catch (error) {
      setMessage(error.message)
      setMessageType('error')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="app">
      <header className="navbar">
        <Link to="/" className="logo">
          CoinMaster
        </Link>

        <Link to="/register" className="primary-button">
          Create Account
        </Link>
      </header>

      <main className="auth-page">
        <section className="auth-card">
          <p className="badge">Welcome Back</p>
          <h2>Login to CoinMaster</h2>

          <p className="auth-description">
            Enter your account information to access your portfolio.
          </p>

          <form className="auth-form" onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="username">Username</label>

              <input
                id="username"
                name="username"
                type="text"
                placeholder="Enter your username"
                value={formData.username}
                onChange={handleChange}
              />
            </div>

            <div className="form-group">
              <label htmlFor="password">Password</label>

              <input
                id="password"
                name="password"
                type="password"
                placeholder="Enter your password"
                value={formData.password}
                onChange={handleChange}
              />
            </div>

            {message && (
              <div className={`form-message ${messageType}`}>
                {message}
              </div>
            )}

            <button type="submit" className="auth-submit-button" disabled={isSubmitting}>
              {isSubmitting ? 'Logging in...' : 'Login'}
            </button>
          </form>

          <p className="auth-footer">
            Don&apos;t have an account?{' '}
            <Link to="/register">Register here</Link>
          </p>
        </section>
      </main>
    </div>
  )
}

export default LoginPage
