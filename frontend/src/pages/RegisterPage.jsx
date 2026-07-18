import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api, saveSession } from '../api'
import '../App.css'

function RegisterPage() {
  const navigate = useNavigate()
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
  })

  const [message, setMessage] = useState('')
  const [messageType, setMessageType] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  function handleChange(event) {
    const { name, value } = event.target

    setFormData((currentData) => ({
      ...currentData,
      [name]: value,
    }))
  }

  async function handleSubmit(event) {
    event.preventDefault()

    const {
      username,
      email,
      password,
      confirmPassword,
    } = formData

    if (
      !username.trim() ||
      !email.trim() ||
      !password.trim() ||
      !confirmPassword.trim()
    ) {
      setMessage('Please complete all fields.')
      setMessageType('error')
      return
    }

    if (!email.includes('@')) {
      setMessage('Please enter a valid email address.')
      setMessageType('error')
      return
    }

    if (password.length < 6) {
      setMessage('Password must contain at least 6 characters.')
      setMessageType('error')
      return
    }

    if (password !== confirmPassword) {
      setMessage('Passwords do not match.')
      setMessageType('error')
      return
    }

    try {
      setIsSubmitting(true)
      const session = await api.register({ username, email, password })
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

        <Link to="/login" className="nav-button">
          Login
        </Link>
      </header>

      <main className="auth-page">
        <section className="auth-card">
          <p className="badge">Create Account</p>

          <h2>Join CoinMaster</h2>

          <p className="auth-description">
            Create an account and start managing your crypto portfolio.
          </p>

          <form className="auth-form" onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="username">Username</label>

              <input
                id="username"
                name="username"
                type="text"
                placeholder="Choose a username"
                value={formData.username}
                onChange={handleChange}
              />
            </div>

            <div className="form-group">
              <label htmlFor="email">Email</label>

              <input
                id="email"
                name="email"
                type="email"
                placeholder="Enter your email"
                value={formData.email}
                onChange={handleChange}
              />
            </div>

            <div className="form-group">
              <label htmlFor="password">Password</label>

              <input
                id="password"
                name="password"
                type="password"
                placeholder="Create a password"
                value={formData.password}
                onChange={handleChange}
              />
            </div>

            <div className="form-group">
              <label htmlFor="confirmPassword">
                Confirm Password
              </label>

              <input
                id="confirmPassword"
                name="confirmPassword"
                type="password"
                placeholder="Repeat your password"
                value={formData.confirmPassword}
                onChange={handleChange}
              />
            </div>

            {message && (
              <div className={`form-message ${messageType}`}>
                {message}
              </div>
            )}

            <button type="submit" className="auth-submit-button" disabled={isSubmitting}>
              {isSubmitting ? 'Creating...' : 'Create Account'}
            </button>
          </form>

          <p className="auth-footer">
            Already have an account?{' '}
            <Link to="/login">Login here</Link>
          </p>
        </section>
      </main>
    </div>
  )
}

export default RegisterPage
