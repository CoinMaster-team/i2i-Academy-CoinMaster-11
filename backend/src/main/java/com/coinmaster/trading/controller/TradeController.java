package com.coinmaster.trading.controller;

import com.coinmaster.common.error.ApiErrorResponse;
import com.coinmaster.common.security.CurrentUserProvider;
import com.coinmaster.trading.dto.TradeRequest;
import com.coinmaster.trading.dto.TradeResponse;
import com.coinmaster.trading.service.TradeApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trades")
@Tag(name = "Trading")
@SecurityRequirement(name = "bearerAuth")
public class TradeController {

    private final TradeApplicationService tradeService;
    private final CurrentUserProvider currentUserProvider;

    public TradeController(TradeApplicationService tradeService, CurrentUserProvider currentUserProvider) {
        this.tradeService = tradeService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping("/buy")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Buy a supported cryptocurrency")
    @ApiResponse(responseCode = "201", description = "Trade executed")
    @ApiResponse(responseCode = "422", description = "Insufficient funds", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public TradeResponse buy(@Valid @RequestBody TradeRequest request) {
        return tradeService.buy(currentUserProvider.currentUserId(), request);
    }

    @PostMapping("/sell")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Sell a supported cryptocurrency")
    @ApiResponse(responseCode = "201", description = "Trade executed")
    @ApiResponse(responseCode = "422", description = "Insufficient asset", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public TradeResponse sell(@Valid @RequestBody TradeRequest request) {
        return tradeService.sell(currentUserProvider.currentUserId(), request);
    }
}
