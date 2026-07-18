package com.coinmaster.trading.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "portfolio_positions", uniqueConstraints = {
        @UniqueConstraint(name = "uk_position_user_symbol", columnNames = {"user_id", "symbol"})
})
public class PortfolioPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(nullable = false, length = 10, updatable = false)
    private String symbol;

    @Column(nullable = false, precision = 28, scale = 12)
    private BigDecimal quantity;

    @Version
    @Column(nullable = false)
    private long version;

    protected PortfolioPosition() {
    }

    public PortfolioPosition(UUID userId, String symbol) {
        this.userId = userId;
        this.symbol = symbol;
        this.quantity = BigDecimal.ZERO.setScale(12);
    }

    public void increase(BigDecimal amount) {
        this.quantity = this.quantity.add(amount);
    }

    public void decrease(BigDecimal amount) {
        this.quantity = this.quantity.subtract(amount);
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }
}
