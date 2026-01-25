package com.example.service.impl;

import com.example.entity.User;
import com.example.entity.enums.PrivacyLevel;
import com.example.repository.UserRepository;
import com.example.service.MinioService;
import com.example.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MinioService minioService;

    @Override
    public User getUserById(Long id, Long viewerId) {

        User owner = userRepository.findById(id)
                .orElseThrow();

        User viewer = null;
        if (viewerId != null) {
            viewer = userRepository.findById(viewerId).orElse(null);
        }

        if (!canViewProfile(owner, viewer)) {
            return null;
        }

        return owner;
    }


    @Override
    public User getOrCreateUserFromKeycloak(String username) {

        return userRepository.findByUsername(username)
                .orElseGet(() -> {

                    User u = new User();
                    u.setUsername(username);

                    // ✅ BẮT BUỘC
                    u.setKeycloakId(username); // hoặc auth.getName()

                    u.setCreatedAt(LocalDateTime.now());

                    return userRepository.save(u);
                });
    }




    @Override
    public Long getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow().getId();
    }

    @Transactional
    @Override
    public void updateProfile(Long userId, String bio, PrivacyLevel privacy) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setBio(bio);
        user.setPrivacy(privacy);
    }

    @Override
    @Transactional
    public void uploadAvatar(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId).orElseThrow();

        if (user.getAvatarPath() != null) {
            minioService.deleteObject(user.getAvatarPath());
        }

        String path = minioService.uploadAvatar(userId, file);
        user.setAvatarPath(path);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void follow(Long userId, Long targetId) {

        if (userId.equals(targetId)) return;

        User me = userRepository.findById(userId).orElseThrow();
        User target = userRepository.findById(targetId).orElseThrow();

        target.getFollowers().add(me);
    }

    @Override
    @Transactional
    public void unfollow(Long userId, Long targetId) {

        User me = userRepository.findById(userId).orElseThrow();
        User target = userRepository.findById(targetId).orElseThrow();

        target.getFollowers().remove(me);
    }


    @Override
    public boolean canViewProfile(User owner, User viewer) {

        if (owner.getPrivacy() == PrivacyLevel.PUBLIC) {
            return true;
        }

        if (viewer == null) {
            return false;
        }

        if (owner.getId().equals(viewer.getId())) {
            return true;
        }

        if (owner.getPrivacy() == PrivacyLevel.FRIENDS) {
            return userRepository.isFollower(owner.getId(), viewer.getId());
        }

        return false; // PRIVATE
    }
    public void updateProfile(String username, String bio, String privacy) {
        User user = userRepository.findByUsername(username)
                .orElseThrow();

        user.setBio(bio);

        userRepository.save(user);
    }
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }



}
