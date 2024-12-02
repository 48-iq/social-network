package dev.ivanov.social_network.auth_service.services;

import dev.ivanov.social_network.auth_service.dto.JwtDto;
import dev.ivanov.social_network.auth_service.dto.SignInDto;
import dev.ivanov.social_network.auth_service.dto.SignUpDto;
import dev.ivanov.social_network.auth_service.entities.Account;
import dev.ivanov.social_network.auth_service.exceptions.AccountNotFoundException;
import dev.ivanov.social_network.auth_service.repositories.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class AuthService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    public JwtDto signIn(SignInDto signInDto) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken()

        Optional<Account> accountOptional = accountRepository.findAccountByUsername(signInDto.getUsername());

        if (accountOptional.isEmpty()) {
            throw new AccountNotFoundException("account with username " + signInDto.getUsername() + " not found");
        }

        Account account = accountOptional.get();


        authenticationManager.authenticate()

        JwtDto jwtDto = jwtUtils.generateJwt(account);

        return jwtDto;
    }

    public JwtDto signUp(SignUpDto signUpDto) {

        Account account = accountService.createAccount(signUpDto);

    }
}
