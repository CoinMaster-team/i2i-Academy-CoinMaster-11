package com.coinmaster.ai.market;

import java.util.List;
import java.util.Map;

/** Infrastructure module implements this with recent PostgreSQL price snapshots. */
public interface RecentPriceTrendProvider {

    Map<String, List<PriceTrendPoint>> recentTrends(int limitPerSymbol);
}
