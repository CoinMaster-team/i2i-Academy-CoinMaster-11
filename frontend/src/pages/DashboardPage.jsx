import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import ReactMarkdown from 'react-markdown'
import { api, clearSession, getToken, getUsername } from '../api'
import TradeModal from '../components/TradeModal'
import '../App.css'

const assetNames = {
  BTC: 'Bitcoin',
  ETH: 'Ethereum',
  BNB: 'BNB',
  XRP: 'XRP',
  SOL: 'Solana',
  TRX: 'TRON',
  DOGE: 'Dogecoin',
  USDT: 'Tether',
  USDC: 'USD Coin',
  USDS: 'USDS',
}

const assetIcons = {
  BTC: 'B',
  ETH: 'E',
  BNB: 'N',
  XRP: 'X',
  SOL: 'S',
  TRX: 'T',
  DOGE: 'D',
  USDT: 'T',
  USDC: 'U',
  USDS: 'U',
}

function formatCurrency(value) {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    maximumFractionDigits: 2,
  }).format(Number(value ?? 0))
}

function formatCrypto(value, symbol) {
  return `${Number(value ?? 0).toFixed(6)} ${symbol}`
}

function DashboardPage() {
  const navigate = useNavigate()
  const [message, setMessage] = useState('')
  const [chat, setChat] = useState([])
  const [selectedCrypto, setSelectedCrypto] = useState(null)
  const [tradeType, setTradeType] = useState('')
  const [isTradeModalOpen, setIsTradeModalOpen] = useState(false)
  const [marketPrices, setMarketPrices] = useState([])
  const [portfolio, setPortfolio] = useState(null)
  const [transactions, setTransactions] = useState([])
  const [status, setStatus] = useState({ loading: true, error: '' })
  const [aiLoading, setAiLoading] = useState(false)
  const [aiStatus, setAiStatus] = useState({ configured: false, model: '' })
  const chatEndRef = useRef(null)

  const loadDashboard = useCallback(async () => {
    try {
      setStatus((current) => ({ ...current, loading: true, error: '' }))
      const [prices, portfolioResponse, tradeHistory, aiStatusResponse] = await Promise.all([
        api.marketPrices(),
        api.portfolio(),
        api.trades(20),
        api.aiStatus(),
      ])
      setMarketPrices(prices)
      setPortfolio(portfolioResponse)
      setTransactions(tradeHistory)
      setAiStatus(aiStatusResponse)
    } catch (error) {
      setStatus({ loading: false, error: error.message })
      if (error.message.toLowerCase().includes('token')) {
        clearSession()
        navigate('/login')
      }
      return
    }
    setStatus({ loading: false, error: '' })
  }, [navigate])

  useEffect(() => {
    if (!getToken()) {
      navigate('/login')
      return
    }
    loadDashboard()
    const intervalId = window.setInterval(loadDashboard, 15000)
    return () => window.clearInterval(intervalId)
  }, [loadDashboard, navigate])

  useEffect(() => {
    chatEndRef.current?.scrollIntoView({ behavior: 'smooth', block: 'nearest' })
  }, [chat, aiLoading])

  const holdingsBySymbol = useMemo(() => {
    return new Map((portfolio?.positions ?? []).map((position) => [position.symbol, position]))
  }, [portfolio])

  const marketData = marketPrices.map((price) => {
    const position = holdingsBySymbol.get(price.symbol)
    return {
      name: assetNames[price.symbol] ?? price.symbol,
      symbol: price.symbol,
      icon: assetIcons[price.symbol] ?? price.symbol[0],
      price: price.price,
      formattedPrice: formatCurrency(price.price),
      iconClass: `crypto-icon-${price.symbol.toLowerCase()}`,
      ownedQuantity: position?.quantity ?? 0,
      canSell: Number(position?.quantity ?? 0) > 0,
    }
  })

  const totalPortfolioValue = Number(portfolio?.totalPortfolioValue ?? 0)
  const totalCryptoValue = Number(portfolio?.totalCryptoValue ?? 0)
  const cryptoAllocation = totalPortfolioValue > 0
    ? Math.min(100, Math.max(0, (totalCryptoValue / totalPortfolioValue) * 100))
    : 0
  const lastUpdated = portfolio?.asOf
    ? new Date(portfolio.asOf).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    : 'Syncing'

  async function handleSendMessage() {
    const question = message.trim()
    if (!question || !aiStatus.configured || aiLoading) {
      return
    }

    setChat((current) => [...current, { role: 'user', text: question }])
    setMessage('')
    setAiLoading(true)
    try {
      const history = chat
        .filter((entry) => !entry.isError)
        .slice(-12)
        .map((entry) => ({ role: entry.role, content: entry.text.slice(0, 8000) }))
      const response = await api.askAi(question, history)
      setChat((current) => [...current, { role: 'assistant', text: response.answer }])
    } catch (error) {
      setChat((current) => [...current, { role: 'assistant', text: error.message, isError: true }])
    } finally {
      setAiLoading(false)
    }
  }

  function openTradeModal(crypto, type) {
    setSelectedCrypto(crypto)
    setTradeType(type)
    setIsTradeModalOpen(true)
  }

  function closeTradeModal() {
    setIsTradeModalOpen(false)
    setSelectedCrypto(null)
    setTradeType('')
  }

  async function executeTrade(side, symbol, amount) {
    await api.executeTrade(side, {
      symbol,
      quantity: Number(amount),
      clientOrderId: crypto.randomUUID(),
    })
    await loadDashboard()
  }

  function handleLogout() {
    clearSession()
    navigate('/')
  }

  return (
    <div className="dashboard-app">
      <header className="dashboard-navbar">
        <Link to="/" className="dashboard-logo">
          CoinMaster
        </Link>

        <div className="dashboard-nav-actions">
          <button type="button" className="logout-button" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      <main className="dashboard-container">
        <section className="dashboard-welcome">
          <h1>Welcome Back, {getUsername()}</h1>
          <p>Track your portfolio, trade crypto and get AI-powered insights.</p>
        </section>

        {status.error && <div className="form-message error">{status.error}</div>}
        {status.loading && <div className="loading-strip">Loading live dashboard data...</div>}

        <section className="dashboard-top-grid">
          <article className="dashboard-card balance-panel">
            <div className="balance-heading">
              <div className="balance-title">
                <span className="wallet-icon">$</span>

                <div>
                  <span className="balance-eyebrow">Portfolio value</span>
                  <h2>Total Balance</h2>
                </div>
              </div>

              <span className="balance-updated">
                <span aria-hidden="true" />
                {lastUpdated}
              </span>
            </div>

            <div className="balance-content">
              <div className="balance-total">
                <strong>{formatCurrency(portfolio?.totalPortfolioValue)}</strong>
                <p>Cash and crypto combined</p>
              </div>

              <div className="balance-breakdown">
                <div className="balance-metric">
                  <span>Available cash</span>
                  <strong>{formatCurrency(portfolio?.cashBalance)}</strong>
                </div>

                <div className="balance-metric">
                  <span>Crypto assets</span>
                  <strong>{formatCurrency(portfolio?.totalCryptoValue)}</strong>
                </div>
              </div>
            </div>

            <div className="balance-allocation">
              <div className="allocation-label">
                <span>Portfolio allocation</span>
                <strong>{Math.round(cryptoAllocation)}% crypto</strong>
              </div>

              <div
                className="allocation-track"
                role="progressbar"
                aria-label="Crypto allocation"
                aria-valuemin="0"
                aria-valuemax="100"
                aria-valuenow={Math.round(cryptoAllocation)}
              >
                <span style={{ width: `${cryptoAllocation}%` }} />
              </div>
            </div>
          </article>

          <article className="dashboard-card market-panel">
            <h2>Market Prices</h2>

            <div className="market-grid">
              {marketData.map((cryptoAsset) => (
                <div className="market-card" key={cryptoAsset.symbol}>
                  <div className="crypto-header">
                    <span className={`crypto-icon ${cryptoAsset.iconClass}`}>
                      {cryptoAsset.icon}
                    </span>

                    <div>
                      <h3>{cryptoAsset.name}</h3>
                      <p>{cryptoAsset.symbol}</p>
                    </div>
                  </div>

                  <div className="market-price-row">
                    <div>
                      <strong>{cryptoAsset.formattedPrice}</strong>
                    </div>

                    <div className="price-chart">
                      <span />
                      <span />
                      <span />
                      <span />
                      <span />
                    </div>
                  </div>

                  <div className="trade-buttons">
                    <button
                      type="button"
                      className="buy-button"
                      disabled={Number(portfolio?.cashBalance ?? 0) <= 0}
                      onClick={() => openTradeModal(cryptoAsset, 'Buy')}
                    >
                      Buy
                    </button>

                    <button
                      type="button"
                      className="sell-button"
                      disabled={!cryptoAsset.canSell}
                      onClick={() => openTradeModal(cryptoAsset, 'Sell')}
                    >
                      Sell
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </article>
        </section>

        <section className="dashboard-bottom-grid">
          <article className="dashboard-card portfolio-panel">
            <h2>Portfolio</h2>

            <div className="portfolio-list">
              {(portfolio?.positions ?? []).map((asset) => (
                <div className="portfolio-item" key={asset.symbol}>
                  <div className="portfolio-asset">
                    <span className={`crypto-icon crypto-icon-${asset.symbol.toLowerCase()}`}>
                      {assetIcons[asset.symbol] ?? asset.symbol[0]}
                    </span>

                    <div>
                      <h3>{assetNames[asset.symbol] ?? asset.symbol}</h3>
                      <p>{asset.symbol}</p>
                    </div>
                  </div>

                  <div className="portfolio-value">
                    <strong>{formatCrypto(asset.quantity, asset.symbol)}</strong>
                    <p>{formatCurrency(asset.marketValue)}</p>
                  </div>
                </div>
              ))}

              {!portfolio?.positions?.length && (
                <p className="empty-state">No crypto holdings yet.</p>
              )}
            </div>
          </article>

          <article className="dashboard-card transactions-panel">
            <h2>Recent Transactions</h2>

            <div className="transaction-list">
              {transactions.map((transaction) => (
                <div className="transaction-item" key={transaction.tradeId}>
                  <div className="transaction-main">
                    <span className={`transaction-icon ${transaction.side.toLowerCase()}`}>
                      {transaction.side === 'BUY' ? '+' : '-'}
                    </span>

                    <div>
                      <h3>{transaction.side} {transaction.symbol}</h3>
                      <p>{new Date(transaction.executedAt).toLocaleString()}</p>
                    </div>
                  </div>

                  <div className="transaction-value">
                    <strong className={transaction.side.toLowerCase()}>
                      {formatCrypto(transaction.quantity, transaction.symbol)}
                    </strong>
                    <p>{formatCurrency(transaction.totalAmount)}</p>
                  </div>
                </div>
              ))}

              {!transactions.length && <p className="empty-state">No trades yet.</p>}
            </div>
          </article>

          <article className={`dashboard-card ai-panel ${chat.length ? 'has-conversation' : ''}`}>
            <div className="ai-heading">
              <h2>AI Assistant (Gemini)</h2>
              <span className={`ai-status ${aiStatus.configured ? 'ready' : 'offline'}`}>
                {aiStatus.configured ? `${aiStatus.model} ready` : 'API key required'}
              </span>
            </div>

            {!chat.length && (
              <div className="ai-welcome">
                <span className="ai-icon">AI</span>

                <p>Ask about your account, recent trades, trends or portfolio context.</p>
              </div>
            )}

            {(chat.length > 0 || aiLoading) && (
              <div className="chat-list">
                {chat.map((entry, index) => (
                  <div
                    className={`chat-message ${entry.role} ${entry.isError ? 'error' : ''}`}
                    key={`${entry.role}-${index}`}
                  >
                    {entry.role === 'assistant' ? <ReactMarkdown>{entry.text}</ReactMarkdown> : entry.text}
                  </div>
                ))}
                {aiLoading && <div className="chat-message assistant thinking">Thinking...</div>}
                <div ref={chatEndRef} />
              </div>
            )}

            {!chat.length && (
              <div className="suggestion-list">
                <button
                  type="button"
                  disabled={!aiStatus.configured || aiLoading}
                  onClick={() => setMessage("What's the market trend today?")}
                >
                  What's the market trend today?
                </button>

                <button
                  type="button"
                  disabled={!aiStatus.configured || aiLoading}
                  onClick={() => setMessage('How is my portfolio performing?')}
                >
                  How is my portfolio performing?
                </button>
              </div>
            )}

            <div className="ai-input-row">
              <textarea
                placeholder="Ask Gemini anything..."
                value={message}
                rows={2}
                disabled={!aiStatus.configured || aiLoading}
                onChange={(event) => setMessage(event.target.value)}
                onKeyDown={(event) => {
                  if (event.key === 'Enter' && !event.shiftKey) {
                    event.preventDefault()
                    handleSendMessage()
                  }
                }}
              />

              <button
                type="button"
                disabled={!aiStatus.configured || aiLoading || !message.trim()}
                onClick={handleSendMessage}
                aria-label="Send message"
              >
                Go
              </button>
            </div>
          </article>
        </section>
      </main>

      <TradeModal
        isOpen={isTradeModalOpen}
        onClose={closeTradeModal}
        crypto={selectedCrypto}
        tradeType={tradeType}
        onExecute={executeTrade}
      />
    </div>
  )
}

export default DashboardPage
