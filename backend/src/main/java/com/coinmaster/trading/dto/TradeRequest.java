package com.coinmaster.trading.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record TradeRequest(
        @NotBlank
        @Schema(example = "BTC", allowableValues = {"BTC", "ETH", "BNB", "XRP", "SOL", "TRX", "DOGE", "USDT", "USDC", "USDS"})
        String symbol,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        @Digits(integer = 16, fraction = 12)
        @Schema(example = "0.002500000000", type = "string")
        BigDecimal quantity,

        @NotNull
        @Schema(description = "Frontend-generated id used to prevent duplicate execution")
        UUID clientOrderId
) {
}
