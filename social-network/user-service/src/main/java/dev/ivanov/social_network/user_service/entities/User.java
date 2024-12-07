package dev.ivanov.social_network.user_service.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private String id;
    private String nickname;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private Boolean deleted;

    private Image avatar;

    private List<FriendRequest> sentFriendRequests;

    private List<FriendRequest> gottenFriendRequests;

    private List<User> friends;
    
    private List<Image> images;
}
