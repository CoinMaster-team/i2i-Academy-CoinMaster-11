package com.coinmaster.market;

import java.math.BigDecimal;

/** Implemented by the infrastructure module using the latest Redis price keys. */
public interface CurrentPriceProvider {

    BigDecimal getRequiredPrice(String symbol);
}
