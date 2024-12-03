package dev.ivanov.social_network.auth_service.validators;

import dev.ivanov.social_network.auth_service.dto.ChangePasswordDto;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ChangePasswordDtoValidator implements Validator {

    @Override
    public boolean supports(@Nonnull Class<?> clazz) {
        return ChangePasswordDto.class.equals(clazz);
    }

    @Override
    public void validate(@Nonnull Object target, @Nonnull Errors errors) {
        ChangePasswordDto changePasswordDto = (ChangePasswordDto) target;
        String password = changePasswordDto.getPassword();
        if (password == null || !password.matches("^.{8,128}$"))
            errors.reject("password", "password is incorrect");
    }
}
