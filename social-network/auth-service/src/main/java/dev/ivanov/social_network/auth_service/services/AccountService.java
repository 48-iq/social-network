package dev.ivanov.social_network.auth_service.services;

import dev.ivanov.social_network.auth_service.dto.JwtDto;
import dev.ivanov.social_network.auth_service.dto.SignUpDto;
import dev.ivanov.social_network.auth_service.entities.Account;
import dev.ivanov.social_network.auth_service.entities.Role;
import dev.ivanov.social_network.auth_service.events.Actions;
import dev.ivanov.social_network.auth_service.exceptions.AccountNotFoundException;
import dev.ivanov.social_network.auth_service.exceptions.EventNotSentException;
import dev.ivanov.social_network.auth_service.exceptions.RemoteServiceException;
import dev.ivanov.social_network.auth_service.producers.AccountEventsProducer;
import dev.ivanov.social_network.auth_service.repositories.AccountRepository;
import dev.ivanov.social_network.auth_service.repositories.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Value("${app.gateway.uri}")
    private String gatewayUri;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccountEventsProducer accountEventsProducer;

    @Transactional
    public Account createAccount(SignUpDto signUpDto) {
        String uuidServiceUri = gatewayUri + "/api/uuid";
        ResponseEntity<String> generatedIdEntity = restTemplate.getForEntity(uuidServiceUri, String.class);

        if (generatedIdEntity.getStatusCode().isError()) {
            throw new RemoteServiceException("uuid service error, code: " + generatedIdEntity.getStatusCode());
        }

        String generatedId = generatedIdEntity.getBody();

        log.trace("uuid {} has been created", generatedId);

        List<Role> roles = new ArrayList<>();

        for (String roleName: signUpDto.getRoles()) {
            Role role = roleRepository.getReferenceById(roleName);
            roles.add(role);
        }

        Account account = Account.builder()
                .id(generatedId)
                .username(signUpDto.getUsername())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .createdAt(ZonedDateTime.now().toInstant())
                .deleted(false)
                .roles(roles)
                .build();

        Account savedAccount = accountRepository.save(account);

        try {
            accountEventsProducer.send(Actions.CREATE, account.getId());
        } catch (EventNotSentException e) {
            deleteAccount(account.getId());
        }

        log.trace("account {} has been created", account.getId());
        return savedAccount;
    }

    @Transactional
    public void changePassword(String accountId, String password) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("account with id " + accountId + " not found"));
        account.setPassword(passwordEncoder.encode(password));
        accountRepository.save(account);
    }

    @Transactional
    public void deleteAccount(String accountId) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);

        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            account.setDeleted(true);
            accountRepository.save(account);

            log.trace("account {} has been deleted", account.getId());
        }
    }

}
