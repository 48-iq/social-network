package dev.ivanov.social_network.user_service.services;

import dev.ivanov.social_network.user_service.dto.ProfileInfoUpdateDto;
import dev.ivanov.social_network.user_service.entities.User;
import dev.ivanov.social_network.user_service.exceptions.UserNotFoundException;
import dev.ivanov.social_network.user_service.repositories.ImageRepository;
import dev.ivanov.social_network.user_service.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Transactional
    public void updateProfileInfo(String userId, ProfileInfoUpdateDto profileInfoUpdateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("user with id " + userId + " not found"));

        user.setNickname(profileInfoUpdateDto.getNickname());
        user.setName(profileInfoUpdateDto.getName());
        user.setSurname(profileInfoUpdateDto.getSurname());
        user.setEmail(profileInfoUpdateDto.getEmail());
        user.setPhone(profileInfoUpdateDto.getPhone());

        userRepository.save(user);
    }

    @Transactional
    public void setProfileAvatar(String userId, String imageId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("user with id " + userId + " not found"));

        user.setAvatar(imageRepository.getReferenceById(imageId));

        userRepository.save(user);
    }
}
