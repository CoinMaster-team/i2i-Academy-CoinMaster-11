package com.coinmaster.market.repository;

import com.coinmaster.market.entity.PriceHistory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, UUID> {

    List<PriceHistory> findBySymbolOrderByCapturedAtDesc(String symbol, Pageable pageable);
}
