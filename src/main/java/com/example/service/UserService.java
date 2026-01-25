package com.example.service;

import com.example.entity.User;
import com.example.entity.enums.PrivacyLevel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    User getUserById(Long id, Long viewerId);

    User getOrCreateUserFromKeycloak(String username);

    Long getUserIdByUsername(String username);

    void updateProfile(Long userId, String bio, PrivacyLevel privacy);

    void uploadAvatar(Long userId, MultipartFile file);

    void follow(Long userId, Long targetId);

    boolean canViewProfile(User owner, User viewer);

    void unfollow(Long userId, Long targetId);

    List<User> findAll();



}
