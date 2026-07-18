package com.coinmaster.trading.entity;

import com.coinmaster.trading.domain.TradeSide;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "trade_transactions", uniqueConstraints = {
        @UniqueConstraint(name = "uk_trade_user_client_order", columnNames = {"user_id", "client_order_id"})
})
public class TradeTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 4, updatable = false)
    private TradeSide side;

    @Column(nullable = false, length = 10, updatable = false)
    private String symbol;

    @Column(nullable = false, precision = 28, scale = 12, updatable = false)
    private BigDecimal quantity;

    @Column(name = "execution_price", nullable = false, precision = 19, scale = 8, updatable = false)
    private BigDecimal executionPrice;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2, updatable = false)
    private BigDecimal totalAmount;

    @Column(name = "client_order_id", nullable = false, updatable = false)
    private UUID clientOrderId;

    @Column(name = "executed_at", nullable = false, updatable = false)
    private Instant executedAt;

    protected TradeTransaction() {
    }

    public TradeTransaction(UUID userId, TradeSide side, String symbol, BigDecimal quantity,
                            BigDecimal executionPrice, BigDecimal totalAmount, UUID clientOrderId,
                            Instant executedAt) {
        this.userId = userId;
        this.side = side;
        this.symbol = symbol;
        this.quantity = quantity;
        this.executionPrice = executionPrice;
        this.totalAmount = totalAmount;
        this.clientOrderId = clientOrderId;
        this.executedAt = executedAt;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public TradeSide getSide() { return side; }
    public String getSymbol() { return symbol; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getExecutionPrice() { return executionPrice; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public UUID getClientOrderId() { return clientOrderId; }
    public Instant getExecutedAt() { return executedAt; }
}
