package com.coinmaster.trading.dto;

import com.coinmaster.trading.domain.TradeSide;
import com.coinmaster.trading.entity.PortfolioPosition;
import com.coinmaster.trading.entity.TradeTransaction;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TradeResponse(
        UUID tradeId,
        TradeSide side,
        String symbol,
        BigDecimal quantity,
        BigDecimal executionPrice,
        BigDecimal totalAmount,
        BigDecimal cashBalanceAfter,
        BigDecimal assetQuantityAfter,
        Instant executedAt
) {
    public static TradeResponse from(
            TradeTransaction trade,
            BigDecimal cashBalance,
            PortfolioPosition position
    ) {
        return new TradeResponse(
                trade.getId(),
                trade.getSide(),
                trade.getSymbol(),
                trade.getQuantity(),
                trade.getExecutionPrice(),
                trade.getTotalAmount(),
                cashBalance,
                position.getQuantity(),
                trade.getExecutedAt()
        );
    }
}
