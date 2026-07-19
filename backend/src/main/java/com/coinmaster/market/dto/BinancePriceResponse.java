package com.coinmaster.market.dto;

public class BinancePriceResponse {

    private String symbol;
    private String price;

    public BinancePriceResponse() {
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
