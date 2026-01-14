package com.example.service;

import com.example.entity.UserFollow;
import com.example.entity.Userprofile;
import com.example.repository.UserFollowRepository;
import com.example.repository.UserprofileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFollowService {

    private final UserFollowRepository followRepository;
    private final UserprofileRepository userRepository;

    public void follow(String followerKeycloakId, String followingKeycloakId) {

        Userprofile follower = userRepository.findByKeycloakId(followerKeycloakId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));

        Userprofile following = userRepository.findByKeycloakId(followingKeycloakId)
                .orElseThrow(() -> new RuntimeException("Following not found"));

        // Đã follow rồi thì bỏ qua
        if (followRepository.existsByFollowerIdAndFollowingId(
                follower.getId(), following.getId())) {
            return;
        }

        UserFollow uf = new UserFollow();
        uf.setFollower(follower);
        uf.setFollowing(following);

        followRepository.save(uf);
    }

    public void unfollow(String followerKeycloakId, String followingKeycloakId) {

        Userprofile follower = userRepository.findByKeycloakId(followerKeycloakId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));

        Userprofile following = userRepository.findByKeycloakId(followingKeycloakId)
                .orElseThrow(() -> new RuntimeException("Following not found"));

        followRepository.deleteByFollowerIdAndFollowingId(
                follower.getId(), following.getId()
        );
    }
}

