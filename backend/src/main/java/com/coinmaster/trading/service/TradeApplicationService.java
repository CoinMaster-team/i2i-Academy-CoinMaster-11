package com.coinmaster.trading.service;

import com.coinmaster.market.CurrentPriceProvider;
import com.coinmaster.trading.domain.AssetSymbol;
import com.coinmaster.trading.dto.TradeRequest;
import com.coinmaster.trading.dto.TradeResponse;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TradeApplicationService {

    private final CurrentPriceProvider priceProvider;
    private final TradeExecutionService executionService;

    public TradeApplicationService(CurrentPriceProvider priceProvider, TradeExecutionService executionService) {
        this.priceProvider = priceProvider;
        this.executionService = executionService;
    }

    public TradeResponse buy(UUID userId, TradeRequest request) {
        String symbol = AssetSymbol.parse(request.symbol()).name();
        BigDecimal price = priceProvider.getRequiredPrice(symbol);
        return executionService.buy(userId, symbol, request.quantity(), price, request.clientOrderId());
    }

    public TradeResponse sell(UUID userId, TradeRequest request) {
        String symbol = AssetSymbol.parse(request.symbol()).name();
        BigDecimal price = priceProvider.getRequiredPrice(symbol);
        return executionService.sell(userId, symbol, request.quantity(), price, request.clientOrderId());
    }
}
