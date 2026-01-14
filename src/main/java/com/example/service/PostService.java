package com.example.service;

import com.example.entity.Post;
import com.example.entity.Userprofile;
import com.example.repository.PostRepository;
import com.example.repository.UserprofileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepo;
    private final UserprofileRepository userRepo;

    public Post createPost(String keycloakId, String content) {

        Userprofile user = userRepo.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setUserId(user.getId()); // ✅ BIGINT
        post.setContent(content);

        return postRepo.save(post);
    }

    public Post updatePost(Long postId, String keycloakId, String content) {

        Userprofile user = userRepo.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // ✅ LONG - LONG
        if (!post.getUserId().equals(user.getId())) {
            throw new AccessDeniedException("Not owner of post");
        }

        post.setContent(content);
        return postRepo.save(post);
    }

    public void deletePost(Long postId, String keycloakId) {

        Userprofile user = userRepo.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUserId().equals(user.getId())) {
            throw new AccessDeniedException("Not owner of post");
        }

        postRepo.delete(post);
    }

    public Post getPost(Long id) {
        return postRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }
}
