package com.coinmaster.market;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public final class SupportedSymbols {

    public static final List<String> ALL = List.of(
            "BTC",
            "ETH",
            "BNB",
            "XRP",
            "SOL",
            "TRX",
            "DOGE",
            "USDT",
            "USDC",
            "USDS"
    );

    public static final Map<String, String> BINANCE_USDT_PAIRS = Map.of(
            "BTC", "BTCUSDT",
            "ETH", "ETHUSDT",
            "BNB", "BNBUSDT",
            "XRP", "XRPUSDT",
            "SOL", "SOLUSDT",
            "TRX", "TRXUSDT",
            "DOGE", "DOGEUSDT"
    );

    public static final Map<String, BigDecimal> INITIAL_PRICES = Map.of(
            "BTC", new BigDecimal("65000.00000000"),
            "ETH", new BigDecimal("3500.00000000"),
            "BNB", new BigDecimal("600.00000000"),
            "XRP", new BigDecimal("0.60000000"),
            "SOL", new BigDecimal("150.00000000"),
            "TRX", new BigDecimal("0.12000000"),
            "DOGE", new BigDecimal("0.12000000"),
            "USDT", new BigDecimal("1.00000000"),
            "USDC", new BigDecimal("1.00000000"),
            "USDS", new BigDecimal("1.00000000")
    );

    public static final Map<String, BigDecimal> LOCAL_MAX_CHANGES = Map.of(
            "BTC", new BigDecimal("500.00"),
            "ETH", new BigDecimal("50.00"),
            "BNB", new BigDecimal("8.00"),
            "XRP", new BigDecimal("0.02"),
            "SOL", new BigDecimal("4.00"),
            "TRX", new BigDecimal("0.004"),
            "DOGE", new BigDecimal("0.006")
    );

    private SupportedSymbols() {
    }
}
