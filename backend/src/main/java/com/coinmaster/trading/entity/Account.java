package com.coinmaster.trading.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "cash_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal cashBalance;

    @Version
    @Column(nullable = false)
    private long version;

    protected Account() {
    }

    public Account(UUID userId, BigDecimal cashBalance) {
        this.userId = userId;
        this.cashBalance = cashBalance;
    }

    public void debit(BigDecimal amount) {
        this.cashBalance = this.cashBalance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        this.cashBalance = this.cashBalance.add(amount);
    }

    public UUID getUserId() {
        return userId;
    }

    public BigDecimal getCashBalance() {
        return cashBalance;
    }
}
