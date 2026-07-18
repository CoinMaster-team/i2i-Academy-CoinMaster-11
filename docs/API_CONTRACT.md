# CoinMaster API Contract

All protected endpoints use `Authorization: Bearer <session-token>` after the infrastructure/auth branch is merged.
While running the standalone `local` profile, send `X-User-Id: <uuid>` instead.

Financial JSON values are decimal numbers. The frontend must avoid converting high-precision crypto quantities to imprecise display values.

## Trading

### `POST /api/v1/trades/buy`

### `POST /api/v1/trades/sell`

```json
{
  "symbol": "BTC",
  "quantity": 0.002500000000,
  "clientOrderId": "dd5982d8-152f-45fe-9991-cf838e035482"
}
```

The frontend generates a new `clientOrderId` for each user action and must reuse it only when retrying the exact same request.

Successful response (`201 Created`):

```json
{
  "tradeId": "c042603d-c5f2-4bfd-b0ad-07255a9d21a4",
  "side": "BUY",
  "symbol": "BTC",
  "quantity": 0.002500000000,
  "executionPrice": 65000.00000000,
  "totalAmount": 162.50,
  "cashBalanceAfter": 4837.50,
  "assetQuantityAfter": 0.002500000000,
  "executedAt": "2026-07-15T10:30:00Z"
}
```

## Portfolio

### `GET /api/v1/portfolio`

Returns cash, non-zero positions, Redis-backed current prices and calculated market values.

### `GET /api/v1/trades?limit=20`

Returns only the authenticated user's most recent trades. `limit` is clamped to `1..100`.

## AI Insights

### `POST /api/v1/ai/insights`

```json
{
  "question": "Summarize my portfolio and recent transactions."
}
```

Successful response:

```json
{
  "answer": "Markdown returned by Gemini",
  "generatedAt": "2026-07-15T10:32:00Z",
  "format": "MARKDOWN",
  "disclaimer": "This content is informational and is not financial advice."
}
```

## Standard error

```json
{
  "timestamp": "2026-07-15T10:30:00Z",
  "status": 422,
  "code": "INSUFFICIENT_FUNDS",
  "message": "Insufficient cash balance to complete this trade",
  "path": "/api/v1/trades/buy",
  "traceId": "f72e9c12",
  "fieldErrors": {}
}
```

Frontend logic should branch on `code`, not the human-readable `message`.
