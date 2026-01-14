package com.example.controller;

import com.example.entity.UserBio;
import com.example.service.UserBioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserBioService userBioService;

    @PutMapping("/{id}/bio")
    public ResponseEntity<UserBio> updateBio(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String bio = body.get("bio");
        if (bio == null) {
            return ResponseEntity.badRequest().build();
        }

        UserBio updated = userBioService.updateBio(id, bio);
        return ResponseEntity.ok(updated);
    }
}
