package com.coinmaster.portfolio.controller;

import com.coinmaster.common.security.CurrentUserProvider;
import com.coinmaster.portfolio.dto.PortfolioResponse;
import com.coinmaster.portfolio.service.PortfolioService;
import com.coinmaster.trading.dto.TradeHistoryItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Portfolio")
@SecurityRequirement(name = "bearerAuth")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final CurrentUserProvider currentUserProvider;

    public PortfolioController(PortfolioService portfolioService, CurrentUserProvider currentUserProvider) {
        this.portfolioService = portfolioService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/portfolio")
    @Operation(summary = "Get the current user's cash and crypto portfolio")
    public PortfolioResponse portfolio() {
        return portfolioService.getPortfolio(currentUserProvider.currentUserId());
    }

    @GetMapping("/trades")
    @Operation(summary = "Get the current user's recent trade history")
    public List<TradeHistoryItem> history(@RequestParam(defaultValue = "20") int limit) {
        return portfolioService.getTradeHistory(currentUserProvider.currentUserId(), limit);
    }
}
