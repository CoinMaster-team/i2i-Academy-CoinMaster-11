package com.coinmaster.trading.repository;

import com.coinmaster.trading.entity.TradeTransaction;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeTransactionRepository extends JpaRepository<TradeTransaction, UUID> {

    Optional<TradeTransaction> findByUserIdAndClientOrderId(UUID userId, UUID clientOrderId);

    List<TradeTransaction> findByUserIdOrderByExecutedAtDesc(UUID userId, Pageable pageable);
}
