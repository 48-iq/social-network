package dev.ivanov.social_network.auth_service.services;

import dev.ivanov.social_network.auth_service.dto.SignUpDto;
import dev.ivanov.social_network.auth_service.entities.Account;
import dev.ivanov.social_network.auth_service.entities.Role;
import dev.ivanov.social_network.auth_service.repositories.AccountRepository;
import dev.ivanov.social_network.auth_service.repositories.RoleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

public class AccountServiceTests {

    AccountRepository accountRepository;
    RestTemplate restTemplate;
    RoleRepository roleRepository;
    AccountService accountService;
    PasswordEncoder passwordEncoder;
    String gatewayUri = "http://gateway:8080";

    @BeforeEach
    public void setup() {
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        accountRepository = Mockito.mock(AccountRepository.class);
        restTemplate = Mockito.mock(RestTemplate.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        accountService = new AccountService(
                accountRepository,
                gatewayUri,
                restTemplate,
                passwordEncoder,
                roleRepository
        );
    }


    @Test
    public void whenCreateAccount_thenSaveAccountInDb() {

        String roleName = "ROLE_USER";

        Role role = Role.builder()
                .name(roleName)
                .build();

        String uuidServiceUri = gatewayUri + "/uuid";

        String testId = "test_id";

        Account account = Account.builder()
                .id(testId)
                .username("test_username")
                .password("test_password")
                .roles(List.of(role))
                .build();

        Mockito.when(passwordEncoder.encode("test_password")).thenReturn("encoded_test_password");
        Mockito.when(roleRepository.getReferenceById(roleName)).thenReturn(role);
        Mockito.when(restTemplate.getForEntity(uuidServiceUri, String.class)).thenReturn(ResponseEntity.ok(testId));
        Mockito.when(accountRepository.save(Mockito.any(Account.class))).thenReturn(account);

        SignUpDto signUpDto = SignUpDto.builder()
                .username("test_username")
                .password("test_password")
                .roles(List.of("ROLE_USER"))
                .build();

        Account accountCreationResult = accountService.createAccount(signUpDto);
        Assertions.assertEquals(account.getId(), accountCreationResult.getId());
        Mockito.verify(accountRepository).save(Mockito.any(Account.class));
    }

    @Test
    public void whenDeleteAccount_thenDeleteAccountInDb() {
        String accountId = "test_id";

        Account account = Account.builder()
                .id(accountId)
                .username("test_username")
                .password("test_password")
                .deleted(false)
                .build();

        Mockito.when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountService.deleteAccount(accountId);

        Assertions.assertTrue(account.getDeleted());

        Mockito.verify(accountRepository).save(account);

    }
}
