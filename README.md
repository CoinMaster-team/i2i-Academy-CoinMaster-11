# CoinMaster

CoinMaster is a modular Spring Boot and React application for simulated cryptocurrency prices, transactional buying/selling, portfolio tracking and Gemini-powered insights.

## Repository layout

```text
backend/   Spring Boot modular monolith
frontend/  React + Vite SPA
docs/      Team integration contracts
```

The `feature/trading-ai` branch owns the trading, portfolio, AI insights, shared API errors and OpenAPI documentation. The infrastructure branch owns PostgreSQL/Redis containers, users/authentication, Redis session validation, the price ticker and historical price persistence.

## Trading/AI endpoints

| Method | Path | Purpose |
|---|---|---|
| POST | `/api/v1/trades/buy` | Buy BTC or ETH using the server-side Redis price |
| POST | `/api/v1/trades/sell` | Sell an owned BTC or ETH quantity |
| GET | `/api/v1/portfolio` | Current cash and crypto valuation |
| GET | `/api/v1/trades?limit=20` | Recent immutable ledger entries |
| GET | `/api/v1/ai/status` | Gemini configuration status without exposing the API key |
| POST | `/api/v1/ai/insights` | Gemini answer enriched with the user's portfolio and trades |

See [docs/API_CONTRACT.md](docs/API_CONTRACT.md) for frontend payloads and error codes.

## Requirements

- Java 17+
- Maven 3.9+
- PostgreSQL
- Redis (after the infrastructure adapter is merged)
- Node.js compatible with the frontend's Vite version

## Configuration

The root `.env` file is loaded automatically when the backend starts from either the repository root or the `backend` directory. Set `GEMINI_API_KEY` there; never commit `.env` or a Gemini API key. The default text model is `gemini-3.5-flash`.

The database migration in this branch is deliberately numbered `V100`: the infrastructure branch must create `users(id UUID ...)` in an earlier Flyway migration. `accounts`, `portfolio_positions` and `trade_transactions` reference that table.

Required integration implementations outside this branch:

- `CurrentUserProvider`: resolves `Authorization: Bearer <session-token>` through Redis.
- `CurrentPriceProvider`: reads the absolute latest BTC/ETH values from Redis.
- `RecentPriceTrendProvider`: reads bounded historical snapshots from PostgreSQL.

The default `local` Spring profile supplies an in-memory H2 database, `X-User-Id` authentication, fixed BTC/ETH prices and a demo account so the module can be developed before those adapters are merged. The demo user ID is `11111111-1111-1111-1111-111111111111` and its starting balance is `5000.00`.

## Run the backend

For standalone development, no database container is required:

```bash
cd backend
mvn spring-boot:run
```

Swagger UI: `http://localhost:8080/swagger-ui.html`

When using the standalone local profile, add `X-User-Id: 11111111-1111-1111-1111-111111111111` to requests. In the integrated application, select a non-local profile, registration creates the account, PostgreSQL/Flyway are enabled and the Redis-backed bearer token identifies the user.

## Test

```bash
cd backend
mvn test
```

Tests use H2 in PostgreSQL compatibility mode, disable Flyway, and validate buy/sell bookkeeping, insufficient-funds behavior, duplicate order protection and prompt boundaries.

## Financial integrity rules

- PostgreSQL is the source of truth for cash, positions and ledger entries.
- Redis is never used for persistent portfolio state.
- The frontend cannot submit an execution price or user ID.
- Money and quantities use `BigDecimal`, never floating-point arithmetic.
- Account and position updates plus ledger insertion share one transaction.
- Account rows are pessimistically locked to prevent concurrent overspending.
- Gemini calls happen outside database transactions and have a strict timeout.

## Frontend development

```bash
cd frontend
npm install
npm run dev
```

The current frontend contains authentication forms but no API calls yet. Integrate it against the payloads in `docs/API_CONTRACT.md` and render backend errors based on the stable `code` field.
