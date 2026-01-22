package com.example.controller.admin;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

@RestController
@RequiredArgsConstructor
public class AvatarController {

    private final MinioClient minioClient;
    private static final String BUCKET = "thanhnguyen";

    @GetMapping(value = "/avatar/{filename}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<InputStreamResource> getAvatar(
            @PathVariable String filename) throws Exception {

        InputStream is = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(BUCKET)
                        .object("avatar/" + filename)
                        .build()
        );

        return ResponseEntity.ok(new InputStreamResource(is));
    }
}


