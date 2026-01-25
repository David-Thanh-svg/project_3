package com.example.controller.user;

import com.example.entity.PostMedia;
import com.example.service.MinioService;
import com.example.repository.PostMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class MediaController {

    private final PostMediaRepository postMediaRepository;
    private final MinioService minioService;

    @GetMapping("/media/{id}")
    public ResponseEntity<Void> viewMedia(@PathVariable Long id) {

        PostMedia media = postMediaRepository.findById(id)
                .orElseThrow();

        String presignedUrl =
                minioService.getPresignedUrl(media.getMediaPath());

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(presignedUrl))
                .build();
    }
}

