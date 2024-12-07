package dev.ivanov.social_network.user_service.services;

import dev.ivanov.social_network.user_service.dto.ImageDto;
import dev.ivanov.social_network.user_service.entities.Image;
import dev.ivanov.social_network.user_service.entities.User;
import dev.ivanov.social_network.user_service.exceptions.FileNotLoadedException;
import dev.ivanov.social_network.user_service.exceptions.FileNotSavedException;
import dev.ivanov.social_network.user_service.exceptions.ImageNotExistsException;
import dev.ivanov.social_network.user_service.exceptions.RemoteServiceException;
import dev.ivanov.social_network.user_service.repositories.ImageRepository;
import dev.ivanov.social_network.user_service.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ImageService {

    @Value("${app.gateway.uri}")
    private String gatewayUri;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public String loadImage(String userId, ImageDto imageDto) {

        String uuidServiceUri = gatewayUri + "/api/uuid";

        ResponseEntity<String> imageIdEntity = restTemplate.getForEntity(uuidServiceUri, String.class);

        if (imageIdEntity.getStatusCode().isError()) {
            throw new RemoteServiceException("uuid service error");
        }

        String imageId = imageIdEntity.getBody();

        try {
            Path dirPath = Path.of("/images");
            String imagePath = "/images/" + imageId;

            if (Files.notExists(dirPath)) {
                Files.createDirectory(dirPath);
            }

            try (OutputStream outputStream = new FileOutputStream(imagePath)) {
                outputStream.write(imageDto.getImage().getBytes());

                User user = userRepository.getReferenceById(userId);

                Image image = Image.builder()
                        .id(imageId)
                        .filepath(imagePath)
                        .user(user)
                        .build();

                imageRepository.save(image);
                return imageId;

            }
        } catch (IOException e) {
            log.error("image save error: {}", e.getMessage());
            throw new FileNotSavedException(e);
        }
    }

    public InputStream getImage(String imageId) {
        if (Files.notExists(Path.of("/images")))
            throw new ImageNotExistsException("image with id " + imageId + " doesn't exists");

        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageNotExistsException("image with id " + imageId + " doesn't exists"));

        String imagePath = image.getFilepath();

        try {
            InputStream inputStream = new FileInputStream(imagePath);
            return inputStream;
        } catch (IOException e) {
            log.error("image load error: {}", e.getMessage());
            throw new FileNotLoadedException(e);
        }

    }
}
