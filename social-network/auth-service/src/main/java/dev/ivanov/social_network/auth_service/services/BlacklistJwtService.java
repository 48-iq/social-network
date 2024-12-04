package dev.ivanov.social_network.auth_service.services;

import com.auth0.jwt.interfaces.Claim;
import dev.ivanov.social_network.auth_service.entities.BlacklistJwt;
import dev.ivanov.social_network.auth_service.repositories.BlacklistJwtRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistJwtService {

    @Autowired
    private BlacklistJwtRepository blacklistJwtRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${app.jwt.refresh-duration}")
    private Integer refreshDuration;

    public boolean isOnBlacklist(String jwt) {
        Map<String, Claim> claims = jwtUtils.verifyRefreshAndRetrieveClaims(jwt);
        Instant issuedAt = jwtUtils.decodeJwtAndRetrieveIssuedAt(jwt);
        String id = claims.get("id").asString();

        Optional<BlacklistJwt> blacklistJwtOptional = blacklistJwtRepository.findById(id);
        if (blacklistJwtOptional.isEmpty())
            return true;

        BlacklistJwt blacklistJwt = blacklistJwtOptional.get();
        Instant blacklistCheckpoint = blacklistJwt.getTimestamp();

        return blacklistCheckpoint.isBefore(issuedAt);
    }

    public void createBlacklistCheckpoint(String accountId) {
        Instant timestamp = ZonedDateTime.now().toInstant();
        Integer timeToLive = refreshDuration;
        BlacklistJwt blacklistJwt = BlacklistJwt.builder()
                .id(accountId)
                .timestamp(timestamp)
                .timeToLive(timeToLive)
                .build();
        blacklistJwtRepository.save(blacklistJwt);
    }
}
