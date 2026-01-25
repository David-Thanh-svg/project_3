package com.example.service.impl;

import com.example.entity.Post;
import com.example.entity.PostMedia;
import com.example.entity.User;
import com.example.entity.enums.MediaType;
import com.example.entity.enums.PrivacyLevel;
import com.example.repository.PostMediaRepository;
import com.example.repository.PostRepository;
import com.example.repository.UserRepository;
import com.example.service.MinioService;
import com.example.service.PostService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
    private final PostMediaRepository postMediaRepository; // ✅ THÊM


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

        if (files != null) {
            for (MultipartFile f : files) {

                if (f == null || f.isEmpty()) continue;

                String path =
                        minioService.uploadPostMedia(post.getId(), f);

                if (path == null) continue;

                PostMedia media = new PostMedia();
                media.setPost(post);
                media.setMediaPath(path);

                String contentType = f.getContentType();

                if (contentType != null && contentType.startsWith("video")) {
                    media.setType(MediaType.VIDEO);
                } else {
                    media.setType(MediaType.IMAGE);
                }

                post.getMedia().add(media);
                postMediaRepository.save(media);
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

    @Transactional
    @Override
    public void updatePost(Long postId,
                           Long userId,
                           String content,
                           PrivacyLevel privacy,
                           List<MultipartFile> newFiles,
                           List<Long> deleteMediaIds,
                           List<Long> tagUserIds   ) {

        Post post = postRepository.findById(postId)
                .orElseThrow();

        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền sửa post");
        }


        post.setContent(content);
        post.setPrivacy(privacy);

        // 🏷 cập nhật tag users
        if (tagUserIds != null) {

            post.getTaggedUsers().clear();

            List<User> tagged =
                    userRepository.findAllById(tagUserIds);

            post.getTaggedUsers().addAll(tagged);
        }


        // ❌ xoá media
        if (deleteMediaIds != null) {

            for (Long mediaId : deleteMediaIds) {

                PostMedia media =
                        postMediaRepository.findById(mediaId)
                                .orElseThrow();

                if (!media.getPost().getId().equals(postId)) {
                    throw new AccessDeniedException("Not owner");
                }

                minioService.deleteObject(media.getMediaPath());

                post.getMedia().remove(media);
                postMediaRepository.delete(media);
            }
        }



        // ➕ thêm ảnh mới
        if (newFiles != null) {
            for (MultipartFile file : newFiles) {

                if (file == null || file.isEmpty()) continue;

                String path =
                        minioService.uploadPostMedia(postId, file);

                if (path == null) continue;

                PostMedia media = new PostMedia();
                media.setPost(post);
                media.setMediaPath(path);

                String contentType = file.getContentType();

                if (contentType != null && contentType.startsWith("video")) {
                    media.setType(MediaType.VIDEO);
                } else {
                    media.setType(MediaType.IMAGE);
                }

                post.getMedia().add(media);
                postMediaRepository.save(media);
            }
        }

    }
    @Override
    public Post getPostForEdit(Long postId, Long userId) {

        Post post = postRepository.findByIdWithMedia(postId)
                .orElseThrow(() ->
                        new RuntimeException("Post không tồn tại"));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền sửa");
        }

        return post;
    }

}