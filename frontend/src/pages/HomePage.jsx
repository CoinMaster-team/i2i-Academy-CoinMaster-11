import { Link } from 'react-router-dom'
import '../App.css'

function HomePage() {
  return (
    <div className="app">
      <header className="navbar">
        <Link to="/" className="logo">
          CoinMaster
        </Link>

        <nav>
          <Link to="/login" className="nav-button">
            Login
          </Link>

          <Link to="/register" className="primary-button">
            Register
          </Link>
        </nav>
      </header>

      <main className="hero">
        <section className="hero-content">
          <p className="badge">Smart Crypto Trading Platform</p>

          <h2>Manage your crypto portfolio with confidence.</h2>

          <p className="hero-description">
            Follow live cryptocurrency prices, manage your portfolio,
            execute trades and receive AI-powered market insights.
          </p>

          <Link to="/register" className="get-started-button">
            Get Started
          </Link>
        </section>

        <section className="feature-grid">
          <article className="feature-card">
            <h3>Live Market Data</h3>
            <p>Track current BTC and ETH prices in real time.</p>
          </article>

          <article className="feature-card">
            <h3>Buy and Sell</h3>
            <p>Execute simulated cryptocurrency transactions securely.</p>
          </article>

          <article className="feature-card">
            <h3>AI Insights</h3>
            <p>Ask questions and receive personalized market analysis.</p>
          </article>
        </section>
      </main>
    </div>
  )
}

export default HomePage