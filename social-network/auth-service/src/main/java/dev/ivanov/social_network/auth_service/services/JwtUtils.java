package dev.ivanov.social_network.auth_service.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import dev.ivanov.social_network.auth_service.dto.JwtDto;
import dev.ivanov.social_network.auth_service.entities.Account;
import dev.ivanov.social_network.auth_service.entities.Role;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Component
@NoArgsConstructor
@AllArgsConstructor
public class JwtUtils {

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.subject}")
    private String subject;

    @Value("${app.jwt.access-secret}")
    private String accessSecret;

    @Value("${app.jwt.refresh-secret}")
    private String refreshSecret;

    @Value("${app.jwt.access-duration}")
    private Integer accessDuration;

    @Value("${app.jwt.refresh-duration}")
    private Integer refreshDuration;

    public String generateAccess(Account account) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(subject)
                .withIssuedAt(ZonedDateTime.now().toInstant())
                .withExpiresAt(ZonedDateTime.now().plusSeconds(accessDuration).toInstant())
                .withClaim("id", account.getId())
                .withClaim("username", account.getUsername())
                .withClaim("roles", account.getRoles().stream().map(Role::getName).toList())
                .sign(Algorithm.HMAC256(accessSecret));
    }

    public String generateRefresh(Account account) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(subject)
                .withIssuedAt(ZonedDateTime.now().toInstant())
                .withExpiresAt(ZonedDateTime.now().plusSeconds(refreshDuration).toInstant())
                .withClaim("id", account.getId())
                .sign(Algorithm.HMAC256(refreshSecret));
    }

    public JwtDto generateJwt(Account account) {
        return JwtDto.builder()
                .access(generateAccess(account))
                .refresh(generateRefresh(account))
                .build();
    }

    public Map<String, Claim> verifyAccessAndRetrieveClaims(String access) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(accessSecret))
                .withIssuer(issuer)
                .withSubject(subject)
                .withClaimPresence("id")
                .withClaimPresence("username")
                .withClaimPresence("roles")
                .build();

        return jwtVerifier.verify(access).getClaims();
    }

    public Map<String, Claim> verifyRefreshAndRetrieveClaims(String refresh) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(refreshSecret))
                .withIssuer(issuer)
                .withSubject(subject)
                .withClaimPresence("id")
                .build();

        return jwtVerifier.verify(refresh).getClaims();
    }

    public Instant decodeJwtAndRetrieveIssuedAt(String jwt) {
        DecodedJWT decodedJwt = JWT.decode(jwt);
        return decodedJwt.getIssuedAt().toInstant();
    }
}
