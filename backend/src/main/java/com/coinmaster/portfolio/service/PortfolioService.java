package com.coinmaster.portfolio.service;

import com.coinmaster.common.error.BusinessException;
import com.coinmaster.common.error.ErrorCode;
import com.coinmaster.market.CurrentPriceProvider;
import com.coinmaster.portfolio.dto.PortfolioPositionResponse;
import com.coinmaster.portfolio.dto.PortfolioResponse;
import com.coinmaster.trading.dto.TradeHistoryItem;
import com.coinmaster.trading.entity.Account;
import com.coinmaster.trading.repository.AccountRepository;
import com.coinmaster.trading.repository.PortfolioPositionRepository;
import com.coinmaster.trading.repository.TradeTransactionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PortfolioService {

    private final AccountRepository accountRepository;
    private final PortfolioPositionRepository positionRepository;
    private final TradeTransactionRepository tradeRepository;
    private final CurrentPriceProvider priceProvider;

    public PortfolioService(
            AccountRepository accountRepository,
            PortfolioPositionRepository positionRepository,
            TradeTransactionRepository tradeRepository,
            CurrentPriceProvider priceProvider
    ) {
        this.accountRepository = accountRepository;
        this.positionRepository = positionRepository;
        this.tradeRepository = tradeRepository;
        this.priceProvider = priceProvider;
    }

    public PortfolioResponse getPortfolio(UUID userId) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND, "Account was not found"));

        List<PortfolioPositionResponse> positions = positionRepository
                .findByUserIdAndQuantityGreaterThanOrderBySymbol(userId, BigDecimal.ZERO)
                .stream()
                .map(position -> {
                    BigDecimal price = priceProvider.getRequiredPrice(position.getSymbol());
                    BigDecimal value = position.getQuantity().multiply(price).setScale(2, RoundingMode.HALF_EVEN);
                    return new PortfolioPositionResponse(position.getSymbol(), position.getQuantity(), price, value);
                })
                .toList();

        BigDecimal cryptoTotal = positions.stream()
                .map(PortfolioPositionResponse::marketValue)
                .reduce(BigDecimal.ZERO.setScale(2), BigDecimal::add);

        return new PortfolioResponse(
                account.getCashBalance(),
                positions,
                cryptoTotal,
                account.getCashBalance().add(cryptoTotal),
                Instant.now()
        );
    }

    public List<TradeHistoryItem> getTradeHistory(UUID userId, int requestedLimit) {
        int limit = Math.max(1, Math.min(requestedLimit, 100));
        return tradeRepository.findByUserIdOrderByExecutedAtDesc(userId, PageRequest.of(0, limit))
                .stream()
                .map(TradeHistoryItem::from)
                .toList();
    }
}
