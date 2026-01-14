package com.example.storage;



import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class MinioStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket.avatar}")
    private String avatarBucket;

    @Value("${minio.bucket.media}")
    private String mediaBucket;

    public MinioStorageService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    /* =======================
       UPLOAD AVATAR
       ======================= */
    public String uploadAvatar(String username,
                               MultipartFile file,
                               String oldAvatarPath) {

        try {
            // xóa avatar cũ nếu tồn tại
            if (oldAvatarPath != null && !oldAvatarPath.isBlank()) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(avatarBucket)
                                .object(oldAvatarPath)
                                .build()
                );
            }

            String fileName = generateFileName(username, file.getOriginalFilename());
            String objectName = "avatar/" + fileName;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(avatarBucket)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return objectName;

        } catch (Exception e) {
            throw new RuntimeException("Upload avatar failed", e);
        }
    }


    /* =======================
       UPLOAD MEDIA (IMAGE / VIDEO)
       ======================= */
    public String uploadMedia(String username, MultipartFile file) {
        return uploadFile(
                mediaBucket,
                "media",
                username,
                file
        );
    }

    /* =======================
       CORE UPLOAD FUNCTION
       ======================= */
    private String uploadFile(String bucketName,
                              String folder,
                              String username,
                              MultipartFile file) {

        try {
            String fileName = generateFileName(username, file.getOriginalFilename());
            String objectName = folder + "/" + fileName;

            InputStream inputStream = file.getInputStream();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return objectName; // lưu DB

        } catch (Exception e) {
            throw new RuntimeException("Upload file failed", e);
        }
    }

    /* =======================
       GENERATE FILE NAME
       username_timestamp.ext
       ======================= */
    private String generateFileName(String username, String originalFileName) {

        String extension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String time = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        return username + "_" + time + extension;
    }

    /* =======================
       GET FILE URL (optional)
       ======================= */
    public String getFileUrl(String bucket, String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(objectName)
                            .expiry(60 * 60 * 24) // 24h
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Get file url failed", e);
        }
    }
}
