package com.example.controller;

import com.example.service.PostMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostMediaController {

    private final PostMediaService mediaService;

    @PostMapping("/{id}/media")
    public void upload(@PathVariable Long id,
                       @RequestParam("file") MultipartFile file,
                       Authentication authentication) {

        mediaService.uploadMedia(id, file, authentication);
    }
}
