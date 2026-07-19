package com.coinmaster.trading.domain;

import com.coinmaster.common.error.BusinessException;
import com.coinmaster.common.error.ErrorCode;
import java.util.Locale;

public enum AssetSymbol {
    BTC,
    ETH,
    BNB,
    XRP,
    SOL,
    TRX,
    DOGE,
    USDT,
    USDC,
    USDS;

    public static AssetSymbol parse(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(ErrorCode.UNSUPPORTED_SYMBOL, "Asset symbol is required");
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(ErrorCode.UNSUPPORTED_SYMBOL, "Unsupported asset symbol: " + value);
        }
    }
}
