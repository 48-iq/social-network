package dev.ivanov.social_network.user_service.repositories;

import dev.ivanov.social_network.user_service.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query("select u from User u where u.nickname like %:query% or concat(u.name, ' ', u.surname) like %:query% " +
            "or u.email like %:query% or u.phone like %:query%")
    public Page<User> findUsersByNicknameOrNameAndSurnameOrEmailOrPhone(String query, Pageable pageable);
}
