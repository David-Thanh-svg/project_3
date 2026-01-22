package com.example.service.impl;

import com.example.entity.Post;
import com.example.entity.PostMedia;
import com.example.entity.User;
import com.example.entity.enums.MediaType;
import com.example.entity.enums.PrivacyLevel;
import com.example.repository.PostRepository;
import com.example.repository.UserRepository;
import com.example.service.MinioService;
import com.example.service.PostService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final MinioService minioService;

    @Override
    public Post createPost(Long userId,
                           String content,
                           PrivacyLevel privacy,
                           List<MultipartFile> files,
                           List<Long> tagUserIds) {

        User author = userRepository.findById(userId).orElseThrow();

        Post post = new Post();
        post.setAuthor(author);
        post.setContent(content);
        post.setPrivacy(privacy);

        if (tagUserIds != null && !tagUserIds.isEmpty()) {
            post.setTaggedUsers(
                    new HashSet<>(userRepository.findAllById(tagUserIds))
            );
        }

        postRepository.save(post);

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                String path = minioService.uploadPostMedia(userId, file);

                PostMedia media = new PostMedia();
                media.setMediaPath(path);
                media.setType(
                        file.getContentType() != null &&
                                file.getContentType().startsWith("video")
                                ? MediaType.VIDEO
                                : MediaType.IMAGE
                );
                media.setPost(post);

                post.getMedia().add(media);
            }
        }

        return post;
    }

    @Override
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow();

        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("No permission");
        }

        postRepository.delete(post);
    }

    @Override
    public Post repost(Long userId, Long postId, String content) {
        User user = userRepository.findById(userId).orElseThrow();
        Post original = postRepository.findById(postId).orElseThrow();

        Post repost = new Post();
        repost.setAuthor(user);
        repost.setContent(content);
        repost.setOriginalPost(original);
        repost.setPrivacy(original.getPrivacy());

        return postRepository.save(repost);
    }

    @Override
    public List<Post> getMyPosts(Long userId) {
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(userId);
    }


    @Override
    public List<Post> getFeed(Long viewerId) {
        return postRepository.findFeed(viewerId);
    }

}