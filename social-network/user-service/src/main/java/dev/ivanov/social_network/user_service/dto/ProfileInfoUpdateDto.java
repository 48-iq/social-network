package dev.ivanov.social_network.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileInfoUpdateDto {
    private String nickname;
    private String email;
    private String phone;
    private String name;
    private String surname;
    private String birthdate;
}
