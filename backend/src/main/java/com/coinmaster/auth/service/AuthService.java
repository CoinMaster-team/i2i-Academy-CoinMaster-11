package com.coinmaster.auth.service;

import com.coinmaster.auth.dto.AuthResponse;
import com.coinmaster.auth.dto.LoginRequest;
import com.coinmaster.auth.dto.RegisterRequest;
import com.coinmaster.auth.entity.User;
import com.coinmaster.auth.repository.UserRepository;
import com.coinmaster.auth.session.SessionStore;
import com.coinmaster.common.error.BusinessException;
import com.coinmaster.common.error.ErrorCode;
import com.coinmaster.trading.entity.Account;
import com.coinmaster.trading.repository.AccountRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final SessionStore sessionStore;
    private final BigDecimal startingBalance;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(
            UserRepository userRepository,
            AccountRepository accountRepository,
            SessionStore sessionStore,
            @Value("${coinmaster.account.starting-balance}") BigDecimal startingBalance
    ) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.sessionStore = sessionStore;
        this.startingBalance = startingBalance.setScale(2, RoundingMode.HALF_EVEN);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsernameIgnoreCase(request.username())) {
            throw new BusinessException(ErrorCode.USERNAME_TAKEN, "Username is already taken");
        }
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new BusinessException(ErrorCode.USERNAME_TAKEN, "Email is already registered");
        }

        User user = userRepository.save(new User(
                request.username().trim(),
                request.email().trim(),
                passwordEncoder.encode(request.password())
        ));
        accountRepository.save(new Account(user.getId(), startingBalance));
        String token = sessionStore.create(user.getId(), user.getUsername());
        return new AuthResponse(user.getId(), user.getUsername(), token, startingBalance);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsernameIgnoreCase(request.username().trim())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INVALID_CREDENTIALS, "Invalid username or password"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS, "Invalid username or password");
        }
        String token = sessionStore.create(user.getId(), user.getUsername());
        return new AuthResponse(user.getId(), user.getUsername(), token, null);
    }
}
