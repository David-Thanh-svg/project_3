package com.example.service;

import org.springframework.web.multipart.MultipartFile;

public interface MinioService {

    /**
     * Upload avatar cho user
     * @param userId id user
     * @param file multipart file
     * @return avatar url (path lưu trong DB)
     */
    String uploadAvatar(Long userId, MultipartFile file);

    String uploadPostMedia(Long postId, MultipartFile file); // ✅ THÊM

    /**
     * Xoá object trên MinIO
     */
    void deleteObject(String objectPath);

    /**
     * Tạo presigned url (xem ảnh không cần public bucket)
     */
    String getPresignedUrl(String objectPath);
}

