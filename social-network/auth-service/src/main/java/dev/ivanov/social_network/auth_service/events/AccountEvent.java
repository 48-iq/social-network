package dev.ivanov.social_network.auth_service.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountEvent {
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_CREATE = "create";
    private String action;
    private String creatorId;
    private String accountId;
}
