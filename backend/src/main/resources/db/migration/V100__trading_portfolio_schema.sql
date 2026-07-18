CREATE TABLE accounts (
    user_id UUID PRIMARY KEY,
    cash_balance NUMERIC(19, 2) NOT NULL CHECK (cash_balance >= 0),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE portfolio_positions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    symbol VARCHAR(10) NOT NULL CHECK (symbol IN ('BTC', 'ETH')),
    quantity NUMERIC(28, 12) NOT NULL CHECK (quantity >= 0),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_positions_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uk_position_user_symbol UNIQUE (user_id, symbol)
);

CREATE TABLE trade_transactions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    side VARCHAR(4) NOT NULL CHECK (side IN ('BUY', 'SELL')),
    symbol VARCHAR(10) NOT NULL CHECK (symbol IN ('BTC', 'ETH')),
    quantity NUMERIC(28, 12) NOT NULL CHECK (quantity > 0),
    execution_price NUMERIC(19, 8) NOT NULL CHECK (execution_price > 0),
    total_amount NUMERIC(19, 2) NOT NULL CHECK (total_amount > 0),
    client_order_id UUID NOT NULL,
    executed_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_trades_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uk_trade_user_client_order UNIQUE (user_id, client_order_id)
);

CREATE INDEX idx_trades_user_executed_at
    ON trade_transactions (user_id, executed_at DESC);
