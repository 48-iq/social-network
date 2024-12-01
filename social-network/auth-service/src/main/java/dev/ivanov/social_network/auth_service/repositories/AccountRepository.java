package dev.ivanov.social_network.auth_service.repositories;

import dev.ivanov.social_network.auth_service.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    @Query("select a from Account a where a.username = :username and not a.deleted")
    Optional<Account> findAccountByUsername(String username);
}
