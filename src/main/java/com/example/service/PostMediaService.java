package com.example.service;

import com.example.entity.Post;
import com.example.entity.PostMedia;
import com.example.entity.Userprofile;
import com.example.repository.PostMediaRepository;
import com.example.repository.PostRepository;
import com.example.repository.UserprofileRepository;
import com.example.security.SecurityUtil;
import com.example.storage.MinioStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostMediaService {

    private final PostRepository postRepo;
    private final PostMediaRepository mediaRepo;
    private final UserprofileRepository userRepo;
    private final MinioStorageService storageService;

    public void uploadMedia(Long postId,
                            MultipartFile file,
                            Authentication authentication) {

        // 1️⃣ Lấy keycloakId
        String keycloakId = SecurityUtil.getKeycloakId(authentication);

        // 2️⃣ Lấy user từ DB (Optional → Entity)
        Userprofile user = userRepo.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3️⃣ Lấy post
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // 4️⃣ Check ownership (LONG vs LONG)
        if (!post.getUserId().equals(user.getId())) {
            throw new AccessDeniedException("Not owner of post");
        }

        // 5️⃣ Xác định loại file
        String contentType = file.getContentType();
        String type = (contentType != null && contentType.startsWith("video"))
                ? "video"
                : "image";

        // 6️⃣ Upload MinIO
        String objectName = storageService.uploadMedia(
                user.getId().toString(), file
        );
        if (objectName == null || objectName.isBlank()) {
            throw new RuntimeException("Upload media failed");
        }

        // 7️⃣ Save DB
        PostMedia media = new PostMedia();
        media.setPostId(postId);
        media.setUrl(objectName);
        media.setType(type);

        mediaRepo.save(media);
    }
}


