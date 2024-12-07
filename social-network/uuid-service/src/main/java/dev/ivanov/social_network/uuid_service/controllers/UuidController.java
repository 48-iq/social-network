package dev.ivanov.social_network.uuid_service.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/uuid")
public class UuidController {
    @GetMapping
    public ResponseEntity<String> generate() {
        String uuid = UUID.randomUUID().toString();
        return ResponseEntity.ok(uuid);
    }
}
