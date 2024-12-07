package dev.ivanov.social_network.user_service.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/friend")
public class FriendController {
    @PostMapping("/send-request")
    public ResponseEntity<?> sendRequest(@RequestParam("user_id") String userId) {
        return null;
    }

    @PostMapping("/reply-to-request")
    public ResponseEntity<?> replyToRequest(@RequestParam("request_id") String requestId,
                                            @RequestParam Boolean response) {
        return null;
    }

    @PostMapping("/remove")
    public ResponseEntity<?> remove(@RequestParam("user_id") String userId) {
        return null;
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(required = false) String query,
                                    @RequestParam String page,
                                    @RequestParam String size) {

        return null;
    }

    @DeleteMapping("/cancel-request")
    public ResponseEntity<?> cancelRequest(@RequestParam("request_id") String requestId) {
        return null;
    }



}
