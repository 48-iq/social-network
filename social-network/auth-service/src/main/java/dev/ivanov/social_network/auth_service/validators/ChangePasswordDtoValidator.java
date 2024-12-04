package dev.ivanov.social_network.auth_service.validators;

import dev.ivanov.social_network.auth_service.dto.ChangePasswordDto;
import dev.ivanov.social_network.auth_service.entities.Account;
import dev.ivanov.social_network.auth_service.repositories.AccountRepository;
import dev.ivanov.social_network.auth_service.security.UserDetailsImpl;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class ChangePasswordDtoValidator implements Validator {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public boolean supports(@Nonnull Class<?> clazz) {
        return ChangePasswordDto.class.equals(clazz);
    }

    @Override
    public void validate(@Nonnull Object target, @Nonnull Errors errors) {
        ChangePasswordDto changePasswordDto = (ChangePasswordDto) target;

        String password = changePasswordDto.getPassword();
        String encodedPreviousPassword = passwordEncoder.encode(changePasswordDto.getPreviousPassword());

        if (password == null || !password.matches("^.{8,128}$"))
            errors.reject("password", "password is incorrect");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Account account = userDetails.getAccount();

        if (!account.getPassword().equals(encodedPreviousPassword))
            errors.reject("previousPassword", "previous password is incorrect");
    }
}
