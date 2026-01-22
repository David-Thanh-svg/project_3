package com.example.service.impl;

import com.example.service.MinioService;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Override
    public String uploadAvatar(Long userId, MultipartFile file) {
        try {
            String ext = file.getOriginalFilename()
                    .substring(file.getOriginalFilename().lastIndexOf("."));

            String objectPath = "avatar/" + userId + "/" + UUID.randomUUID() + ext;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectPath)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return objectPath;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteObject(String objectPath) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectPath)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPresignedUrl(String objectPath) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(objectPath)
                            .method(Method.GET)
                            .expiry(60 * 60)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public String uploadPostMedia(Long postId, MultipartFile file) {
        try {
            String ext = file.getOriginalFilename()
                    .substring(file.getOriginalFilename().lastIndexOf("."));

            String objectPath =
                    "posts/" + postId + "/" + UUID.randomUUID() + ext;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectPath)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return objectPath;

        } catch (Exception e) {
            throw new RuntimeException("Upload post media failed", e);
        }
    }

}
