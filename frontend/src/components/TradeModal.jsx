import { useEffect, useState } from 'react'
import '../App.css'

function TradeModal({ isOpen, onClose, crypto, tradeType, onExecute }) {
  const [amount, setAmount] = useState('')
  const [message, setMessage] = useState('')
  const [messageType, setMessageType] = useState('')
  const isSell = tradeType?.toUpperCase() === 'SELL'
  const ownedQuantity = Number(crypto?.ownedQuantity ?? 0)

  function showInsufficientAssetMessage() {
    setMessage(`Bu kadar coin yoktur. Satılabilir miktar: ${ownedQuantity.toFixed(6)} ${crypto.symbol}.`)
    setMessageType('error')
  }

  function updateAmount(nextAmount) {
    if (isSell && nextAmount && Number(nextAmount) > ownedQuantity) {
      showInsufficientAssetMessage()
      return
    }
    setAmount(nextAmount)
    if (messageType === 'error') {
      setMessage('')
      setMessageType('')
    }
  }

  useEffect(() => {
    if (isOpen) {
      setAmount('')
      setMessage('')
      setMessageType('')
    }
  }, [isOpen, crypto, tradeType])

  useEffect(() => {
    function handleEscape(event) {
      if (event.key === 'Escape') {
        onClose()
      }
    }

    if (isOpen) {
      document.addEventListener('keydown', handleEscape)
      document.body.style.overflow = 'hidden'
    }

    return () => {
      document.removeEventListener('keydown', handleEscape)
      document.body.style.overflow = ''
    }
  }, [isOpen, onClose])

  if (!isOpen || !crypto) {
    return null
  }

  function increaseAmount() {
    const currentAmount = Number(amount) || 0
    const nextAmount = currentAmount + 0.001
    if (isSell && nextAmount > ownedQuantity) {
      showInsufficientAssetMessage()
      return
    }
    updateAmount(nextAmount.toFixed(6))
  }

  function decreaseAmount() {
    const currentAmount = Number(amount) || 0
    const newAmount = Math.max(0, currentAmount - 0.001)
    updateAmount(newAmount.toFixed(6))
  }

  async function handleSubmit(event) {
    event.preventDefault()

    const numericAmount = Number(amount)

    if (!amount || numericAmount <= 0) {
      setMessage('Please enter a valid amount.')
      setMessageType('error')
      return
    }

    if (isSell && numericAmount > ownedQuantity) {
      showInsufficientAssetMessage()
      return
    }

    try {
      await onExecute(tradeType, crypto.symbol, amount)
      setMessage(`${tradeType} order for ${amount} ${crypto.symbol} executed.`)
      setMessageType('success')
    } catch (error) {
      setMessage(error.message)
      setMessageType('error')
    }
  }

  return (
    <div
      className="trade-modal-overlay"
      onMouseDown={onClose}
    >
      <section
        className="trade-modal-window"
        onMouseDown={(event) => event.stopPropagation()}
      >
        <button
          type="button"
          className="trade-modal-close"
          onClick={onClose}
          aria-label="Close trade modal"
        >
          ×
        </button>

        <div className="trade-modal-header">
          <span className={`crypto-icon ${crypto.iconClass}`}>
            {crypto.icon}
          </span>

          <div>
            <p className="trade-modal-subtitle">
              {tradeType} Order
            </p>

            <h2>
              {tradeType} {crypto.name}
            </h2>
          </div>
        </div>

        <div className={`trade-modal-summary ${isSell ? 'split' : ''}`}>
          <div className="trade-summary-item">
            <span>Current price</span>
            <strong>{crypto.formattedPrice}</strong>
          </div>

          {isSell && (
            <div className="trade-summary-item available">
              <span>Available to sell</span>
              <strong>{ownedQuantity.toFixed(6)} {crypto.symbol}</strong>
            </div>
          )}
        </div>

        <form
          className="trade-modal-form"
          onSubmit={handleSubmit}
        >
          <div className="form-group">
            <label htmlFor="tradeAmount">
              Amount ({crypto.symbol})
            </label>

            <div className="amount-input-wrapper">
              <input
                id="tradeAmount"
                type="number"
                min="0"
                max={isSell ? ownedQuantity : undefined}
                step="0.000001"
                placeholder={`Enter ${crypto.symbol} amount`}
                value={amount}
                onChange={(event) => updateAmount(event.target.value)}
                autoFocus
              />

              <div className="amount-stepper">
                <button
                  type="button"
                  aria-label="Increase amount"
                  onClick={increaseAmount}
                >
                  ▲
                </button>

                <button
                  type="button"
                  aria-label="Decrease amount"
                  onClick={decreaseAmount}
                >
                  ▼
                </button>
              </div>
            </div>
          </div>

          {message && (
            <div className={`form-message ${messageType}`}>
              {message}
            </div>
          )}

          <button
            type="submit"
            className={`trade-execute-button ${tradeType.toLowerCase()}`}
          >
            Execute Order
          </button>
        </form>
      </section>
    </div>
  )
}

export default TradeModal
