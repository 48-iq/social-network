package dev.ivanov.social_network.auth_service.services;

import dev.ivanov.social_network.auth_service.dto.JwtDto;
import dev.ivanov.social_network.auth_service.dto.RefreshDto;
import dev.ivanov.social_network.auth_service.dto.SignInDto;
import dev.ivanov.social_network.auth_service.dto.SignUpDto;
import dev.ivanov.social_network.auth_service.entities.Account;
import dev.ivanov.social_network.auth_service.exceptions.AccountNotFoundException;
import dev.ivanov.social_network.auth_service.repositories.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    public JwtDto signIn(SignInDto signInDto) {

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(signInDto.getUsername(),
                signInDto.getPassword());

        authenticationManager.authenticate(authentication);

        Optional<Account> accountOptional = accountRepository.findAccountByUsername(signInDto.getUsername());

        if (accountOptional.isEmpty()) {
            throw new AccountNotFoundException("account with username " + signInDto.getUsername() + " not found");
        }

        Account account = accountOptional.get();
        JwtDto jwtDto = jwtUtils.generateJwt(account);

        log.trace("{} has signed in into account", signInDto.getUsername());
        return jwtDto;
    }

    public JwtDto signUp(SignUpDto signUpDto) {

        Account account = accountService.createAccount(signUpDto);
        JwtDto jwtDto = jwtUtils.generateJwt(account);

        log.trace("{} has signed up an account", signUpDto.getUsername());
        return jwtDto;
    }

    public JwtDto refresh(RefreshDto refreshDto) {

    }

    public JwtDto changePassword() {

    }
}
