# CoinMaster

CoinMaster is a full-stack simulated cryptocurrency trading platform. Users can create an account, follow simulated BTC and ETH prices, execute buy/sell transactions, track their portfolio, and receive AI-powered insights through Google Gemini.

> CoinMaster is an educational project. It does not execute real cryptocurrency transactions or provide financial advice.

## Features

- User registration and login
- Token-based session management
- Simulated BTC and ETH market prices
- Cryptocurrency buy and sell operations
- Cash balance and portfolio tracking
- Recent transaction history
- AI-powered portfolio and market insights with Google Gemini
- Swagger/OpenAPI documentation
- PostgreSQL and Redis support for production
- Railway deployment configuration

## Technology Stack

### Backend

- Java 17
- Spring Boot 3.4
- Spring Data JPA
- PostgreSQL
- Redis
- Flyway
- H2 Database for local development and tests
- SpringDoc OpenAPI
- Maven

### Frontend

- React 19
- Vite 8
- React Router
- React Markdown
- CSS
- Nginx for production

## Project Structure

```text
.
├── backend/                 Spring Boot REST API
│   ├── src/main/java/       Application source code
│   ├── src/main/resources/  Configuration and database migrations
│   ├── src/test/            Backend tests
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                React and Vite application
│   ├── src/components/      Reusable UI components
│   ├── src/pages/           Application pages
│   ├── src/assets/          Frontend assets
│   ├── Dockerfile
│   └── package.json
├── docs/                    API contracts
├── docker-compose.yml       PostgreSQL and Redis services
├── DEPLOYMENT.md            Railway deployment guide
└── .env.example             Example environment variables
```

## Requirements

Install the following tools before running the project:

- Java 17 or later
- Maven 3.9 or later
- Node.js 22 or later
- npm

Docker is optional for local development. The default local profile uses an in-memory H2 database and does not require PostgreSQL or Redis.

## Local Development

### 1. Clone the repository

```bash
git clone https://github.com/CoinMaster-team/i2i-Academy-CoinMaster-11.git
cd i2i-Academy-CoinMaster-11
```

### 2. Configure environment variables

Copy `.env.example` as `.env`:

```powershell
Copy-Item .env.example .env
```

The Gemini integration is optional. To enable it, add your API key to `.env`:

```env
GEMINI_API_KEY=your-api-key
```

Never commit the `.env` file or an API key.

### 3. Run the backend

Open a terminal in the project directory:

```bash
cd backend
mvn spring-boot:run
```

The backend starts at:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

The local profile provides:

- An in-memory H2 database
- In-memory user sessions
- Simulated BTC and ETH prices
- A demo account

Demo credentials:

```text
Username: demo
Password: password
```

Local data is cleared when the backend is restarted.

### 4. Run the frontend

Open another terminal in the project directory:

```bash
cd frontend
npm install
npm run dev
```

Open the application at:

```text
http://localhost:5173
```

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/auth/register` | Create a user account |
| `POST` | `/api/v1/auth/login` | Log in and create a session |
| `GET` | `/api/v1/market/prices` | Get simulated BTC and ETH prices |
| `GET` | `/api/v1/portfolio` | Get the current portfolio |
| `GET` | `/api/v1/trades?limit=20` | Get recent transactions |
| `POST` | `/api/v1/trades/buy` | Buy BTC or ETH |
| `POST` | `/api/v1/trades/sell` | Sell BTC or ETH |
| `GET` | `/api/v1/ai/status` | Check Gemini configuration |
| `POST` | `/api/v1/ai/insights` | Request an AI-powered insight |

Detailed request and response examples are available in [docs/API_CONTRACT.md](docs/API_CONTRACT.md).

## Running the Tests

```bash
cd backend
mvn test
```

The backend tests validate trading operations, balance controls, transaction consistency, duplicate-order protection, and AI prompt boundaries.

## PostgreSQL and Redis

The production profile uses PostgreSQL for persistent application data and Redis for market prices and sessions.

Start the local infrastructure containers with:

```bash
docker compose up -d
```

Stop them with:

```bash
docker compose down
```

## Environment Variables

| Variable | Description | Default |
|---|---|---|
| `DB_URL` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/coinmaster` |
| `DB_USERNAME` | PostgreSQL username | `coinmaster` |
| `DB_PASSWORD` | PostgreSQL password | `coinmaster` |
| `FRONTEND_ORIGIN` | Allowed frontend origin | `http://localhost:5173` |
| `STARTING_BALANCE` | Initial account balance | `100000.00` |
| `GEMINI_API_KEY` | Google Gemini API key | Empty |
| `GEMINI_MODEL` | Gemini model name | `gemini-3.5-flash` |
| `GEMINI_TIMEOUT_MS` | Gemini request timeout | `90000` |
| `FLYWAY_ENABLED` | Enables database migrations | `true` |

## Deployment

The repository contains Dockerfiles and Railway configuration for the frontend and backend.

See [DEPLOYMENT.md](DEPLOYMENT.md) for deployment instructions.

## Financial Integrity

- PostgreSQL is the source of truth for balances, positions, and transactions.
- Redis is not used for persistent portfolio data.
- Execution prices are determined by the backend.
- Monetary values and crypto quantities use `BigDecimal`.
- Account updates and ledger records are committed in the same database transaction.
- Account rows are locked during trading operations to prevent concurrent overspending.
- AI responses are informational and are not financial advice.