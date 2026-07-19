package com.coinmaster.market.controller;

import com.coinmaster.market.CurrentPriceProvider;
import com.coinmaster.market.SupportedSymbols;
import com.coinmaster.market.dto.MarketPriceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/market")
@Tag(name = "Market Data")
public class MarketController {

    private final CurrentPriceProvider priceProvider;

    public MarketController(CurrentPriceProvider priceProvider) {
        this.priceProvider = priceProvider;
    }

    @GetMapping("/prices")
    @Operation(summary = "Fetch latest supported cryptocurrency prices")
    public List<MarketPriceResponse> prices() {
        Instant asOf = Instant.now();
        return SupportedSymbols.ALL.stream()
                .map(symbol -> new MarketPriceResponse(symbol, priceProvider.getRequiredPrice(symbol), asOf))
                .toList();
    }
}
