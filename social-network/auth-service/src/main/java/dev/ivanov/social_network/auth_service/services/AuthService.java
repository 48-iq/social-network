package dev.ivanov.social_network.auth_service.services;

import com.auth0.jwt.interfaces.Claim;
import dev.ivanov.social_network.auth_service.dto.*;
import dev.ivanov.social_network.auth_service.entities.Account;
import dev.ivanov.social_network.auth_service.exceptions.AccountNotFoundException;
import dev.ivanov.social_network.auth_service.exceptions.ActionWithDeletedAccountException;
import dev.ivanov.social_network.auth_service.repositories.AccountRepository;
import dev.ivanov.social_network.auth_service.repositories.BlacklistJwtRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Map;
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

    @Autowired
    private BlacklistJwtService blacklistJwtService;

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

    @Transactional
    public JwtDto refresh(RefreshDto refreshDto) {
        String refresh = refreshDto.getRefresh();

        Map<String, Claim> claims = jwtUtils.verifyRefreshAndRetrieveClaims(refresh);
        String id = claims.get("id").asString();

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("account with id " + id + " not found"));

        if (account.getDeleted()) {
            throw new ActionWithDeletedAccountException("account with id " + id + " has been deleted");
        }

        String access = jwtUtils.generateAccess(account);
        JwtDto jwtDto = JwtDto.builder()
                .access(access)
                .build();

        log.trace("account {} has refreshed jwt", account.getUsername());

        return jwtDto;
    }


    public void changePassword(String accountId, ChangePasswordDto changePasswordDto) {
        String password = changePasswordDto.getPassword();
        accountService.changePassword(accountId, password);
        blacklistJwtService.createBlacklistCheckpoint(accountId);
        log.trace("account {} has changed password", accountId);
    }
}
