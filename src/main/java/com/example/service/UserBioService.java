package com.example.service;

import com.example.entity.UserBio;
import com.example.entity.Userprofile;
import com.example.repository.UserBioRepository;
import com.example.repository.UserprofileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserBioService {

    private final UserBioRepository userBioRepository;
    private final UserprofileRepository userprofileRepository;

    public UserBio updateBio(Long userId, String bio) {
        Userprofile user = userprofileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserBio userBio = userBioRepository.findById(userId)
                .orElseGet(() -> {
                    UserBio ub = new UserBio();
                    ub.setUser(user); // VERY IMPORTANT
                    ub.setCreatedAt(LocalDateTime.now());
                    return ub;
                });

        userBio.setBio(bio);
        userBio.setUpdatedAt(LocalDateTime.now());

        return userBioRepository.save(userBio);
    }
}
