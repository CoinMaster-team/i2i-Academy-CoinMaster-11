# CoinMaster Railway Deployment

Public application: b

CoinMaster is deployed as four services in one Railway project:

- `frontend`: public React/Nginx service
- `backend`: private Spring Boot service
- `Postgres`: managed PostgreSQL database
- `Redis`: managed Redis database

The frontend proxies `/api/*` to the backend over Railway private networking, so only the frontend needs a public domain.

## Backend variables

Configure these variables on the `backend` service:

```text
SPRING_PROFILES_ACTIVE=prod
PGHOST=${{Postgres.PGHOST}}
PGPORT=${{Postgres.PGPORT}}
PGDATABASE=${{Postgres.PGDATABASE}}
PGUSER=${{Postgres.PGUSER}}
PGPASSWORD=${{Postgres.PGPASSWORD}}
REDIS_URL=${{Redis.REDIS_URL}}
GEMINI_API_KEY=<Gemini API key>
GEMINI_MODEL=gemini-3.5-flash
STARTING_BALANCE=100000.00
FRONTEND_ORIGIN=https://${{frontend.RAILWAY_PUBLIC_DOMAIN}}
```

Keep `GEMINI_API_KEY` sealed. Do not upload the local root `.env` file.

## Frontend variables

The frontend defaults to a backend service named `backend`. Set this explicitly if the service has a different name:

```text
BACKEND_URL=http://backend.railway.internal:8080
```

Generate a public domain for `frontend` under Railway Networking after both application services are healthy.
