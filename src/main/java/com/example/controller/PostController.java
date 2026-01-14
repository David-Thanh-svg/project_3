package com.example.controller;

import com.example.dto.PostRequest;
import com.example.dto.PostResponse;
import com.example.entity.Post;
import com.example.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public PostResponse create(@AuthenticationPrincipal Jwt jwt,
                               @RequestBody PostRequest req) {

        Post post = postService.createPost(
                jwt.getSubject(),
                req.getContent()
        );

        return toResponse(post);
    }

    @PutMapping("/{id}")
    public PostResponse update(@AuthenticationPrincipal Jwt jwt,
                               @PathVariable Long id,
                               @RequestBody PostRequest req) {

        Post post = postService.updatePost(
                id,
                jwt.getSubject(),
                req.getContent()
        );

        return toResponse(post);
    }

    @DeleteMapping("/{id}")
    public void delete(@AuthenticationPrincipal Jwt jwt,
                       @PathVariable Long id) {
        postService.deletePost(id, jwt.getSubject());
    }

    @GetMapping("/{id}")
    public PostResponse get(@PathVariable Long id) {
        return toResponse(postService.getPost(id));
    }

    private PostResponse toResponse(Post p) {
        return new PostResponse(
                p.getId(),
                p.getUserId(),
                p.getContent(),
                p.isShared(),
                p.getOriginalPostId(),
                p.getCreatedAt()
        );
    }
}
