package dev.ivanov.social_network.auth_service.repositories;

import dev.ivanov.social_network.auth_service.entities.BlacklistJwt;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistJwtRepository extends CrudRepository<BlacklistJwt, String> {
}
