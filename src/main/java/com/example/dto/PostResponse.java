package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class PostResponse {

    private Long id;
    private Long userId;   // ✅ ĐỔI SANG LONG
    private String content;
    private boolean shared;
    private Long originalPostId;
    private Instant createdAt;

    public PostResponse(Long id,
                        Long userId,
                        String content,
                        boolean shared,
                        Long originalPostId,
                        Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.shared = shared;
        this.originalPostId = originalPostId;
        this.createdAt = createdAt;
    }
}
