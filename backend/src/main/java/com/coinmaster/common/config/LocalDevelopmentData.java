package com.coinmaster.common.config;

import com.coinmaster.auth.entity.User;
import com.coinmaster.auth.repository.UserRepository;
import com.coinmaster.trading.entity.Account;
import com.coinmaster.trading.repository.AccountRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@Profile("local")
public class LocalDevelopmentData {

    public static final UUID DEMO_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Bean
    CommandLineRunner createLocalAccount(
            AccountRepository accountRepository,
            UserRepository userRepository,
            @Value("${coinmaster.account.starting-balance}") BigDecimal startingBalance
    ) {
        return args -> {
            if (!userRepository.existsById(DEMO_USER_ID)) {
                userRepository.save(new User(
                        DEMO_USER_ID,
                        "demo",
                        "demo@coinmaster.local",
                        new BCryptPasswordEncoder().encode("password")
                ));
            }
            if (!accountRepository.existsById(DEMO_USER_ID)) {
                accountRepository.save(new Account(DEMO_USER_ID, startingBalance));
            }
        };
    }
}
