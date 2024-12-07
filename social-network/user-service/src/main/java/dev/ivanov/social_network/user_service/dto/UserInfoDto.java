package dev.ivanov.social_network.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDto {
    private String id;
    private String nickname;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String birthdate;
    private String avatarImageId;
    private List<String> imageIdList;
    
}
