package com.coinmaster.trading.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coinmaster.common.error.BusinessException;
import com.coinmaster.common.error.ErrorCode;
import com.coinmaster.trading.dto.TradeResponse;
import com.coinmaster.trading.entity.Account;
import com.coinmaster.trading.repository.AccountRepository;
import com.coinmaster.trading.repository.PortfolioPositionRepository;
import com.coinmaster.trading.repository.TradeTransactionRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:coinmaster;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
@ActiveProfiles("local")
class TradeExecutionServiceIntegrationTest {

    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Autowired
    private TradeExecutionService service;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PortfolioPositionRepository positionRepository;

    @Autowired
    private TradeTransactionRepository tradeRepository;

    @BeforeEach
    void setUp() {
        tradeRepository.deleteAll();
        positionRepository.deleteAll();
        accountRepository.deleteAll();
        accountRepository.saveAndFlush(new Account(USER_ID, new BigDecimal("1000.00")));
    }

    @Test
    void buyUpdatesCashPositionAndLedgerAtomically() {
        TradeResponse response = service.buy(
                USER_ID,
                "BTC",
                new BigDecimal("0.010000000000"),
                new BigDecimal("50000.00000000"),
                UUID.randomUUID()
        );

        assertThat(response.totalAmount()).isEqualByComparingTo("500.00");
        assertThat(response.cashBalanceAfter()).isEqualByComparingTo("500.00");
        assertThat(response.assetQuantityAfter()).isEqualByComparingTo("0.010000000000");
        assertThat(accountRepository.findById(USER_ID).orElseThrow().getCashBalance())
                .isEqualByComparingTo("500.00");
        assertThat(positionRepository.findAll()).hasSize(1);
        assertThat(tradeRepository.findAll()).hasSize(1);
    }

    @Test
    void insufficientFundsLeaveAllFinancialStateUntouched() {
        UUID clientOrderId = UUID.randomUUID();

        assertThatThrownBy(() -> service.buy(
                USER_ID,
                "BTC",
                new BigDecimal("1.000000000000"),
                new BigDecimal("50000.00000000"),
                clientOrderId
        )).isInstanceOfSatisfying(BusinessException.class, exception ->
                assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INSUFFICIENT_FUNDS));

        assertThat(accountRepository.findById(USER_ID).orElseThrow().getCashBalance())
                .isEqualByComparingTo("1000.00");
        assertThat(positionRepository.findAll()).isEmpty();
        assertThat(tradeRepository.findAll()).isEmpty();
    }

    @Test
    void sellCreditsCashAndDecreasesPosition() {
        service.buy(
                USER_ID,
                "ETH",
                new BigDecimal("0.200000000000"),
                new BigDecimal("1000.00000000"),
                UUID.randomUUID()
        );

        TradeResponse response = service.sell(
                USER_ID,
                "ETH",
                new BigDecimal("0.050000000000"),
                new BigDecimal("1200.00000000"),
                UUID.randomUUID()
        );

        assertThat(response.totalAmount()).isEqualByComparingTo("60.00");
        assertThat(response.cashBalanceAfter()).isEqualByComparingTo("860.00");
        assertThat(response.assetQuantityAfter()).isEqualByComparingTo("0.150000000000");
        assertThat(tradeRepository.findAll()).hasSize(2);
    }

    @Test
    void duplicateClientOrderIdIsRejected() {
        UUID clientOrderId = UUID.randomUUID();
        service.buy(USER_ID, "BTC", new BigDecimal("0.001"), new BigDecimal("50000"), clientOrderId);

        assertThatThrownBy(() -> service.buy(
                USER_ID, "BTC", new BigDecimal("0.001"), new BigDecimal("50000"), clientOrderId))
                .isInstanceOfSatisfying(BusinessException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_ORDER));

        assertThat(tradeRepository.findAll()).hasSize(1);
    }
}
