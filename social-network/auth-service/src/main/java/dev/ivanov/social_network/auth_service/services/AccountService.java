package dev.ivanov.social_network.auth_service.services;

import dev.ivanov.social_network.auth_service.dto.JwtDto;
import dev.ivanov.social_network.auth_service.dto.SignUpDto;
import dev.ivanov.social_network.auth_service.entities.Account;
import dev.ivanov.social_network.auth_service.entities.Role;
import dev.ivanov.social_network.auth_service.exceptions.RemoteServiceException;
import dev.ivanov.social_network.auth_service.repositories.AccountRepository;
import dev.ivanov.social_network.auth_service.repositories.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Value("${app.gateway.uri}")
    private String gatewayUri;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public Account createAccount(SignUpDto signUpDto) {
        String uuidServiceUri = gatewayUri + "/uuid";
        ResponseEntity<String> generatedIdEntity = restTemplate.getForEntity(uuidServiceUri, String.class);

        if (generatedIdEntity.getStatusCode().isError())
            throw new RemoteServiceException("uuid service error, code: " + generatedIdEntity.getStatusCode());

        String generatedId = generatedIdEntity.getBody();

        List<Role> roles = new ArrayList<>();

        for (String roleName: signUpDto.getRoles()) {
            Role role = roleRepository.getReferenceById(roleName);
            roles.add(role);
        }

        Account account = Account.builder()
                .id(generatedId)
                .username(signUpDto.getUsername())
                .password(signUpDto.getPassword())
                .roles(roles)
                .build();

        Account savedAccount = accountRepository.save(account);

        return savedAccount;
    }

    @Transactional
    public void deleteAccount(String accountId) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);

        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            account.setDeleted(true);
            accountRepository.save(account);
        }
    }
}