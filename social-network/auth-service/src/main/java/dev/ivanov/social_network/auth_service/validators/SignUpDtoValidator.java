package dev.ivanov.social_network.auth_service.validators;

import dev.ivanov.social_network.auth_service.dto.SignUpDto;
import dev.ivanov.social_network.auth_service.entities.Role;
import dev.ivanov.social_network.auth_service.repositories.AccountRepository;
import dev.ivanov.social_network.auth_service.repositories.RoleRepository;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class SignUpDtoValidator implements Validator {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Value("${app.security.admin-password}")
    private String adminPassword;

    @Override
    public boolean supports(@Nonnull Class<?> clazz) {
        return SignUpDto.class.equals(clazz);
    }

    @Override
    public void validate(@Nonnull Object target, @Nonnull Errors errors) {
        SignUpDto signUpDto = (SignUpDto) target;

        String username = signUpDto.getUsername();
        String password = signUpDto.getPassword();
        List<String> roleNames = signUpDto.getRoles();
        String validatedAdminPassword = signUpDto.getAdminPassword();

        List<Role> allRoles = roleRepository.findAll();

        if (username == null || !username.matches("^.{1,128}$"))
            errors.reject("username", "username is incorrect");

        if (password == null || !password.matches("^.{8,128}$"))
            errors.reject("password", "password is incorrect");
        if (roleNames == null)
            errors.reject("roles", "roles is incorrect");
        else {
            for (String roleName : roleNames) {
                if (allRoles.stream().noneMatch(role -> role.getName().equals(roleName))) {
                    errors.reject("roles", "role " + roleName + " is incorrect");
                }

                if (roleName.equals("ROLE_ADMIN") && !adminPassword.equals(validatedAdminPassword)) {
                    errors.reject("adminPassword", "admin password is incorrect");
                }
            }
        }

        if (accountRepository.existsAccountByUsername(username)) {
            errors.reject("username", "username already exists");
        }

    }
}
