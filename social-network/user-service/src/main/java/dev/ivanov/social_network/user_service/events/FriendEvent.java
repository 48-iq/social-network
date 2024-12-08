package dev.ivanov.social_network.user_service.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendEvent {

    public static final String ACTION_FRIEND = "friend";
    public static final String ACTION_UNFRIEND = "unfriend";

    private String creatorId;
    private String action;
    private String firstUserId;
    private String secondUserId;
}
