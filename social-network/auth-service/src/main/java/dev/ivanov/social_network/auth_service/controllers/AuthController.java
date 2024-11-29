package dev.ivanov.social_network.auth_service.controllers;

import dev.ivanov.social_network.auth_service.dto.ChangePasswordDto;
import dev.ivanov.social_network.auth_service.dto.RefreshDto;
import dev.ivanov.social_network.auth_service.dto.SignInDto;
import dev.ivanov.social_network.auth_service.dto.SignUpDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody SignInDto signInDto) {

    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto signUpDto) {

    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshDto refreshDto) {

    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {

    }

    @DeleteMapping("/delete-account/{accountId}")
    public ResponseEntity<?> deleteAccount(@PathVariable String accountId) {

    }
}
