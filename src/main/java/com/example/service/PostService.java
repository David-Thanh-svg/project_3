package com.example.service;

import com.example.entity.Post;
import com.example.entity.enums.PrivacyLevel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    Post createPost(Long userId, String content,
                    PrivacyLevel privacy,
                    List<MultipartFile> files,
                    List<Long> tagUserIds);

    void deletePost(Long postId, Long userId);

    Post repost(Long userId, Long postId, String content);

    List<Post> getMyPosts(Long userId);

    List<Post> getFeed(Long viewerId);

    void updatePost(Long postId,
                    Long userId,
                    String content,
                    PrivacyLevel privacy,
                    List<MultipartFile> newFiles,
                    List<Long> deleteMediaIds,
                    List<Long> tagUserIds  );

    Post getPostForEdit(Long postId, Long userId);



}