package dev.ivanov.social_network.auth_service.controllers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import dev.ivanov.social_network.auth_service.dto.*;
import dev.ivanov.social_network.auth_service.entities.Account;
import dev.ivanov.social_network.auth_service.exceptions.AccountNotFoundException;
import dev.ivanov.social_network.auth_service.exceptions.ActionWithDeletedAccountException;
import dev.ivanov.social_network.auth_service.exceptions.BlacklistJwtException;
import dev.ivanov.social_network.auth_service.exceptions.RemoteServiceException;
import dev.ivanov.social_network.auth_service.producers.AccountEventsProducer;
import dev.ivanov.social_network.auth_service.security.UserDetailsImpl;
import dev.ivanov.social_network.auth_service.services.AccountService;
import dev.ivanov.social_network.auth_service.services.AuthService;
import dev.ivanov.social_network.auth_service.validators.ChangePasswordDtoValidator;
import dev.ivanov.social_network.auth_service.validators.SignUpDtoValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private SignUpDtoValidator signUpDtoValidator;

    @Autowired
    private ChangePasswordDtoValidator changePasswordDtoValidator;

    @Autowired
    private AccountEventsProducer accountEventsProducer;

    @Autowired
    private AccountService accountService;

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody SignInDto signInDto) {
        try {
            JwtDto jwtDto = authService.signIn(signInDto);
            return ResponseEntity.ok(jwtDto);

        } catch (AccountNotFoundException | UsernameNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (AuthenticationException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto signUpDto) {
        try {
            Errors errors = new BeanPropertyBindingResult(signUpDto, "signUpDto");
            signUpDtoValidator.validate(signUpDto, errors);

            if (errors.hasErrors())
                return ResponseEntity.badRequest().body(errors.getAllErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage));

            JwtDto jwtDto = authService.signUp(signUpDto);

            return ResponseEntity.ok(jwtDto);

        } catch (RemoteServiceException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshDto refreshDto) {
        try {
            JwtDto jwtDto = authService.refresh(refreshDto);
            return ResponseEntity.ok(jwtDto);

        } catch (JWTVerificationException | ActionWithDeletedAccountException | BlacklistJwtException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        try {
            Errors errors = new BeanPropertyBindingResult(changePasswordDto, "changePasswordDto");
            changePasswordDtoValidator.validate(changePasswordDto, errors);

            if (errors.hasErrors()) {
                return ResponseEntity.badRequest().body(errors.getAllErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage));
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Account account = userDetails.getAccount();

            authService.changePassword(account.getId(), changePasswordDto);

            return ResponseEntity.ok().build();

        } catch (JWTVerificationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete-account/{accountId}")
    public ResponseEntity<?> deleteAccount(@PathVariable String accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.ok().build();
    }
}
