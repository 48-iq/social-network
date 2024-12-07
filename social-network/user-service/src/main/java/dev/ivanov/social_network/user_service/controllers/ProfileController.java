package dev.ivanov.social_network.user_service.controllers;

import dev.ivanov.social_network.user_service.dto.ImageDto;
import dev.ivanov.social_network.user_service.dto.ProfileInfoUpdateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @PutMapping("/update-profile-info")
    public ResponseEntity<?> updateProfileInfo(@RequestBody ProfileInfoUpdateDto profileInfoUpdateDto) {
        return null;
    }

    @PostMapping("/load-profile-avatar")
    public ResponseEntity<?> loadProfileAvatar(ImageDto imageDto) {
        return null;
    }

    @PostMapping("/load-image")
    public ResponseEntity<?> loadImage(ImageDto imageDto) {
        return null;
    }

    @PutMapping("/set-profile-avatar")
    public ResponseEntity<?> setProfileAvatar(@RequestParam("image_id") String imageId) {
        return null;
    }

    @GetMapping("/profile-image/{imageId}")
    public ResponseEntity<?> getProfileImage(@PathVariable String imageId) {
        return null;
    }



}
