CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(80) NOT NULL UNIQUE,
    email VARCHAR(180) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE price_history (
    id UUID PRIMARY KEY,
    symbol VARCHAR(10) NOT NULL CHECK (symbol IN ('BTC', 'ETH')),
    price NUMERIC(19, 8) NOT NULL CHECK (price > 0),
    captured_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_price_history_symbol_captured_at
    ON price_history (symbol, captured_at DESC);
