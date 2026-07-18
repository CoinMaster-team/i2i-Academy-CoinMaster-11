package com.coinmaster.trading.repository;

import com.coinmaster.trading.entity.PortfolioPosition;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PortfolioPositionRepository extends JpaRepository<PortfolioPosition, UUID> {

    List<PortfolioPosition> findByUserIdAndQuantityGreaterThanOrderBySymbol(UUID userId, java.math.BigDecimal quantity);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PortfolioPosition p where p.userId = :userId and p.symbol = :symbol")
    Optional<PortfolioPosition> findByUserIdAndSymbolForUpdate(
            @Param("userId") UUID userId,
            @Param("symbol") String symbol
    );
}
