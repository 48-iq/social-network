package dev.ivanov.social_network.user_service.repositories;

import dev.ivanov.social_network.user_service.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, String> {
}
