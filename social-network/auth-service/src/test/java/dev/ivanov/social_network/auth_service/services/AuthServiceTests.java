package dev.ivanov.social_network.auth_service.services;

import dev.ivanov.social_network.auth_service.dto.JwtDto;
import dev.ivanov.social_network.auth_service.dto.SignInDto;
import dev.ivanov.social_network.auth_service.dto.SignUpDto;
import dev.ivanov.social_network.auth_service.entities.Account;
import dev.ivanov.social_network.auth_service.repositories.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

public class AuthServiceTests {
    AuthenticationManager authenticationManager;
    AuthService authService;
    AccountService accountService;
    AccountRepository accountRepository;
    JwtUtils jwtUtils;
    BlacklistJwtService blacklistJwtService;

    @BeforeEach
    public void setup() {
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        accountService = Mockito.mock(AccountService.class);
        jwtUtils = Mockito.mock(JwtUtils.class);
        accountRepository = Mockito.mock(AccountRepository.class);
        blacklistJwtService = Mockito.mock(BlacklistJwtService.class);
        authService = new AuthService(accountRepository,
                accountService,
                authenticationManager,
                jwtUtils,
                blacklistJwtService);
    }


    @Test
    public void whenSignIn_thenAuthenticateByAuthManager() {
        String testUsername = "test_username";
        String testPassword = "test_password";

        SignInDto signInDto = SignInDto.builder()
                .username(testUsername)
                .password(testPassword)
                .build();

        Account account = Account.builder()
                .id("test_id")
                .username(testUsername)
                .password(testPassword)
                .build();

        JwtDto jwtDto = JwtDto.builder()
                .access("test_access")
                .refresh("test_refresh")
                .build();

        Mockito.when(accountRepository.findAccountByUsername(testUsername)).thenReturn(Optional.of(account));
        Mockito.when(jwtUtils.generateJwt(account)).thenReturn(jwtDto);

        authService.signIn(signInDto);

        Mockito.verify(authenticationManager).authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class));
        Mockito.verify(jwtUtils).generateJwt(account);
    }

    @Test
    public void whenSignUp_thenCreateAccountAndGenerateJwt() {
        String testUsername = "test_username";
        String testPassword = "test_password";

        SignUpDto signUpDto = SignUpDto.builder()
                .username(testUsername)
                .password(testPassword)
                .build();

        Account account = Account.builder()
                .id("test_id")
                .username(testUsername)
                .password(testPassword)
                .build();

        JwtDto jwtDto = JwtDto.builder()
                .access("test_access")
                .refresh("test_refresh")
                .build();

        Mockito.when(accountService.createAccount(signUpDto)).thenReturn(account);
        Mockito.when(jwtUtils.generateJwt(account)).thenReturn(jwtDto);

        authService.signUp(signUpDto);

        Mockito.verify(accountService).createAccount(signUpDto);
        Mockito.verify(jwtUtils).generateJwt(account);
    }




}
