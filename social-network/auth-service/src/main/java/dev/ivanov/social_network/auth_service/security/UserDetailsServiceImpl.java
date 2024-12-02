package dev.ivanov.social_network.auth_service.security;

import dev.ivanov.social_network.auth_service.entities.Account;
import dev.ivanov.social_network.auth_service.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> accountOptional = accountRepository.findAccountByUsername(username);

        if(accountOptional.isEmpty()) {
            throw new UsernameNotFoundException("account with username " + username + " not found");
        }

        Account account = accountOptional.get();
        UserDetails userDetails = new UserDetailsImpl(account);

        return userDetails;
    }
}
