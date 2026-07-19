ALTER TABLE price_history
    DROP CONSTRAINT IF EXISTS price_history_symbol_check;

ALTER TABLE price_history
    ADD CONSTRAINT ck_price_history_symbol
        CHECK (symbol IN ('BTC', 'ETH', 'BNB', 'XRP', 'SOL', 'TRX', 'DOGE', 'USDT', 'USDC', 'USDS'));

ALTER TABLE portfolio_positions
    DROP CONSTRAINT IF EXISTS portfolio_positions_symbol_check;

ALTER TABLE portfolio_positions
    ADD CONSTRAINT ck_portfolio_positions_symbol
        CHECK (symbol IN ('BTC', 'ETH', 'BNB', 'XRP', 'SOL', 'TRX', 'DOGE', 'USDT', 'USDC', 'USDS'));

ALTER TABLE trade_transactions
    DROP CONSTRAINT IF EXISTS trade_transactions_symbol_check;

ALTER TABLE trade_transactions
    ADD CONSTRAINT ck_trade_transactions_symbol
        CHECK (symbol IN ('BTC', 'ETH', 'BNB', 'XRP', 'SOL', 'TRX', 'DOGE', 'USDT', 'USDC', 'USDS'));
