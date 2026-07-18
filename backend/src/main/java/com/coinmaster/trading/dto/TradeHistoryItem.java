package com.coinmaster.trading.dto;

import com.coinmaster.trading.domain.TradeSide;
import com.coinmaster.trading.entity.TradeTransaction;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TradeHistoryItem(
        UUID tradeId,
        TradeSide side,
        String symbol,
        BigDecimal quantity,
        BigDecimal executionPrice,
        BigDecimal totalAmount,
        Instant executedAt
) {
    public static TradeHistoryItem from(TradeTransaction trade) {
        return new TradeHistoryItem(
                trade.getId(), trade.getSide(), trade.getSymbol(), trade.getQuantity(),
                trade.getExecutionPrice(), trade.getTotalAmount(), trade.getExecutedAt()
        );
    }
}
