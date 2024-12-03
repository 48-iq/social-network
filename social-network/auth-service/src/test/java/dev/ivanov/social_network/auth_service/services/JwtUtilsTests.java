package dev.ivanov.social_network.auth_service.services;

import com.auth0.jwt.interfaces.Claim;
import dev.ivanov.social_network.auth_service.entities.Account;
import dev.ivanov.social_network.auth_service.entities.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class JwtUtilsTests {

    JwtUtils jwtUtils;
    Account account;


    @BeforeEach
    public void setup() {
        jwtUtils = new JwtUtils(
                "test_issuer",
                "test_subject",
                "test_access_secret",
                "test_refresh_secret",
                1000,
                1000);

        Role testRole = Role.builder()
                .name("test_role")
                .build();

        account = Account.builder()
                .id("test_id")
                .username("test_username")
                .password("test_password")
                .deleted(false)
                .roles(List.of(testRole))
                .build();
    }

    @Test
    public void generate_thenVerifyAndRetrieveAccess() {
        String access = jwtUtils.generateAccess(account);
        Map<String, Claim> claims = jwtUtils.verifyAccessAndRetrieveClaims(access);
        Assertions.assertEquals("test_id", claims.get("id").asString());
        Assertions.assertEquals("test_username", claims.get("username").asString());
        Assertions.assertEquals(List.of("test_role"), claims.get("roles").asList(String.class));
    }

    @Test
    public void generate_thenVerifyAndRetrieveRefresh() {
        String refresh = jwtUtils.generateRefresh(account);
        Map<String, Claim> claims = jwtUtils.verifyRefreshAndRetrieveClaims(refresh);
        Assertions.assertEquals("test_id", claims.get("id").asString());
    }

}
