package dev.ivanov.social_network.auth_service.entities;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.Instant;

@RedisHash
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlacklistJwt {
    @Id
    private String id;
    private Instant timestamp;
    @TimeToLive
    private Integer timeToLive;
}
