package com.example.service;

import com.example.entity.Avatar;
import com.example.entity.Userprofile;
import com.example.repository.AvatarRepository;
import com.example.repository.UserprofileRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AvatarService {

    private final AvatarRepository avatarRepository;
    private final UserprofileRepository userprofileRepository;
    private final MinioClient minioClient;

    public AvatarService(AvatarRepository avatarRepository, UserprofileRepository userprofileRepository, MinioClient minioClient) {
        this.avatarRepository = avatarRepository;
        this.userprofileRepository = userprofileRepository;
        this.minioClient = minioClient;
    }

    public Avatar uploadAvatar(MultipartFile file) throws Exception {
        // Lấy keycloakId từ Authentication
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String keycloakId = auth.getName();

        Userprofile user = userprofileRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tạo tên file: username + timestamp + randomUUID
        String objectName =
                "avatar/" +
                        user.getUsername() + "_" +
                        System.currentTimeMillis() + "_" +
                        UUID.randomUUID() + ".jpg";

        // Upload lên MinIO
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket("thanhnguyen") // tên bucket
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        // Lưu vào database
        Avatar avatar = new Avatar();
        avatar.setUser(user);
        avatar.setKeycloakId(keycloakId);
        avatar.setObjectName(objectName);
        avatar.setContentType(file.getContentType());
        avatar.setFileSize(file.getSize());
        avatar.setUpdateAt(LocalDateTime.now());
        avatar.setIsActive(true);

        // Cập nhật avatarPath hiện tại của user
        user.setAvatarPath(objectName);
        userprofileRepository.save(user);

        return avatarRepository.save(avatar);
    }
}
