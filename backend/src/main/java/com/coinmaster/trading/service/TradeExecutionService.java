package com.coinmaster.trading.service;

import com.coinmaster.common.error.BusinessException;
import com.coinmaster.common.error.ErrorCode;
import com.coinmaster.trading.domain.TradeSide;
import com.coinmaster.trading.dto.TradeResponse;
import com.coinmaster.trading.entity.Account;
import com.coinmaster.trading.entity.PortfolioPosition;
import com.coinmaster.trading.entity.TradeTransaction;
import com.coinmaster.trading.repository.AccountRepository;
import com.coinmaster.trading.repository.PortfolioPositionRepository;
import com.coinmaster.trading.repository.TradeTransactionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TradeExecutionService {

    private final AccountRepository accountRepository;
    private final PortfolioPositionRepository positionRepository;
    private final TradeTransactionRepository tradeRepository;
    private final Clock clock;

    @Autowired
    public TradeExecutionService(
            AccountRepository accountRepository,
            PortfolioPositionRepository positionRepository,
            TradeTransactionRepository tradeRepository) {
        this(accountRepository, positionRepository, tradeRepository, Clock.systemUTC());
    }

    TradeExecutionService(
            AccountRepository accountRepository,
            PortfolioPositionRepository positionRepository,
            TradeTransactionRepository tradeRepository,
            Clock clock) {
        this.accountRepository = accountRepository;
        this.positionRepository = positionRepository;
        this.tradeRepository = tradeRepository;
        this.clock = clock;
    }

    @Transactional
    public TradeResponse buy(UUID userId, String symbol, BigDecimal quantity, BigDecimal rawPrice, UUID clientOrderId) {
        BigDecimal price = normalizePrice(rawPrice);
        BigDecimal normalizedQuantity = normalizeQuantity(quantity);
        BigDecimal total = calculateTotal(normalizedQuantity, price);

        Account account = lockedAccount(userId);
        rejectDuplicate(userId, clientOrderId);
        if (account.getCashBalance().compareTo(total) < 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_FUNDS,
                    "Insufficient cash balance to complete this trade");
        }

        PortfolioPosition position = lockedPosition(userId, symbol);
        account.debit(total);
        position.increase(normalizedQuantity);
        positionRepository.save(position);

        TradeTransaction trade = saveTrade(
                userId, TradeSide.BUY, symbol, normalizedQuantity, price, total, clientOrderId);
        return TradeResponse.from(trade, account.getCashBalance(), position);
    }

    @Transactional
    public TradeResponse sell(UUID userId, String symbol, BigDecimal quantity, BigDecimal rawPrice,
            UUID clientOrderId) {
        BigDecimal price = normalizePrice(rawPrice);
        BigDecimal normalizedQuantity = normalizeQuantity(quantity);
        BigDecimal total = calculateTotal(normalizedQuantity, price);

        Account account = lockedAccount(userId);
        rejectDuplicate(userId, clientOrderId);
        PortfolioPosition position = positionRepository.findByUserIdAndSymbolForUpdate(userId, symbol)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INSUFFICIENT_ASSET, "No " + symbol + " position exists for this account"));

        if (position.getQuantity().compareTo(normalizedQuantity) < 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_ASSET,
                    "Insufficient asset quantity to complete this trade");
        }

        position.decrease(normalizedQuantity);
        account.credit(total);

        TradeTransaction trade = saveTrade(
                userId, TradeSide.SELL, symbol, normalizedQuantity, price, total, clientOrderId);
        return TradeResponse.from(trade, account.getCashBalance(), position);
    }

    private Account lockedAccount(UUID userId) {
        return accountRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND, "Account was not found"));
    }

    private PortfolioPosition lockedPosition(UUID userId, String symbol) {
        return positionRepository.findByUserIdAndSymbolForUpdate(userId, symbol)
                .orElseGet(() -> new PortfolioPosition(userId, symbol));
    }

    private void rejectDuplicate(UUID userId, UUID clientOrderId) {
        if (tradeRepository.findByUserIdAndClientOrderId(userId, clientOrderId).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_ORDER, "The order has already been processed");
        }
    }

    private TradeTransaction saveTrade(
            UUID userId,
            TradeSide side,
            String symbol,
            BigDecimal quantity,
            BigDecimal price,
            BigDecimal total,
            UUID clientOrderId) {
        TradeTransaction trade = new TradeTransaction(
                userId, side, symbol, quantity, price, total, clientOrderId, Instant.now(clock));
        return tradeRepository.saveAndFlush(trade);
    }

    private BigDecimal normalizeQuantity(BigDecimal quantity) {
        BigDecimal normalized = quantity.setScale(12, RoundingMode.HALF_EVEN);
        if (normalized.signum() <= 0) {
            throw new BusinessException(ErrorCode.INVALID_AMOUNT, "Quantity must be greater than zero");
        }
        return normalized;
    }

    private BigDecimal normalizePrice(BigDecimal price) {
        if (price == null || price.signum() <= 0) {
            throw new BusinessException(ErrorCode.MARKET_PRICE_UNAVAILABLE, "A valid market price is unavailable");
        }
        return price.setScale(8, RoundingMode.HALF_EVEN);
    }

    private BigDecimal calculateTotal(BigDecimal quantity, BigDecimal price) {
        BigDecimal total = quantity.multiply(price).setScale(2, RoundingMode.HALF_EVEN);
        if (total.signum() <= 0) {
            throw new BusinessException(ErrorCode.INVALID_AMOUNT, "The calculated order total is too small");
        }
        return total;
    }
}
