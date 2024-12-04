package dev.ivanov.social_network.auth_service.controllers;

import dev.ivanov.social_network.auth_service.dto.ChangePasswordDto;
import dev.ivanov.social_network.auth_service.dto.RefreshDto;
import dev.ivanov.social_network.auth_service.dto.SignInDto;
import dev.ivanov.social_network.auth_service.dto.SignUpDto;
import dev.ivanov.social_network.auth_service.services.AuthService;
import dev.ivanov.social_network.auth_service.validators.ChangePasswordDtoValidator;
import dev.ivanov.social_network.auth_service.validators.SignUpDtoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private SignUpDtoValidator signUpDtoValidator;

    @Autowired
    private ChangePasswordDtoValidator changePasswordDtoValidator;

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody SignInDto signInDto) {
        return null;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto signUpDto) {
        return null;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshDto refreshDto) {
        return null;
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        return null;
    }

    @DeleteMapping("/delete-account/{accountId}")
    public ResponseEntity<?> deleteAccount(@PathVariable String accountId) {
        return null;
    }
}
